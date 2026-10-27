package com.blackfalcon.netflixnew;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Builds TMDB "discover" requests for titles that are on Netflix (watch provider 8) in a region, parses the answers
 * and applies keyword rules. Pure Java: the network is behind the Http interface so it can be tested with fake answers.
 */
final class Tmdb {
    private Tmdb() {}

    static final int NETFLIX = 8;
    static final String BASE = "https://api.themoviedb.org/3";

    interface Http {
        String get(String url) throws IOException;
    }

    interface KeywordCache {
        String get(String word);
        void put(String word, String ids);
    }

    static final class Cfg {
        String key = "";
        String region = "PK";
        String language = "en-US";
        int pages = 2;
    }

    static final class Title {
        boolean tv;
        long id;
        String title = "", overview = "", poster = "", date = "", rule = "";
        double vote;
        List<Integer> genres = new ArrayList<Integer>();
        boolean fresh;       // first seen in the last check
        boolean newEpisodes; // TV show that started before the window but has new episodes in it

        String key() { return (tv ? "tv:" : "movie:") + id; }

        Map<String, Object> toMap() {
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("tv", tv);
            m.put("id", id);
            m.put("title", title);
            m.put("overview", overview);
            m.put("poster", poster);
            m.put("date", date);
            m.put("rule", rule);
            m.put("vote", vote);
            m.put("genres", new ArrayList<Object>(genres));
            m.put("fresh", fresh);
            m.put("eps", newEpisodes);
            return m;
        }

        static Title fromMap(Map<String, Object> m) {
            Title t = new Title();
            t.tv = Json.bool(m, "tv", false);
            t.id = Json.num(m, "id", 0);
            t.title = Json.str(m, "title", "");
            t.overview = Json.str(m, "overview", "");
            t.poster = Json.str(m, "poster", "");
            t.date = Json.str(m, "date", "");
            t.rule = Json.str(m, "rule", "");
            t.vote = Json.dbl(m, "vote", 0);
            for (Object o : Json.list(m.get("genres"))) if (o instanceof Number) t.genres.add(((Number) o).intValue());
            t.fresh = Json.bool(m, "fresh", false);
            t.newEpisodes = Json.bool(m, "eps", false);
            return t;
        }
    }

    static String day(long millis) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(new java.util.Date(millis));
    }

    private static String enc(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    private static String join(List<?> l, String sep) {
        StringBuilder sb = new StringBuilder();
        for (Object o : l) {
            if (sb.length() > 0) sb.append(sep);
            sb.append(o);
        }
        return sb.toString();
    }

    /** genreIds / keywordIds may be empty. OR is "|" and AND is ",". */
    static String discoverUrl(Cfg c, boolean tv, String from, String to, List<Integer> genreIds, boolean matchAll,
                              List<String> keywordIds, int page) {
        StringBuilder u = new StringBuilder(BASE).append("/discover/").append(tv ? "tv" : "movie");
        u.append("?api_key=").append(enc(c.key));
        u.append("&language=").append(enc(c.language));
        u.append("&include_adult=false");
        u.append("&watch_region=").append(enc(c.region));
        u.append("&with_watch_providers=").append(NETFLIX);
        u.append("&with_watch_monetization_types=flatrate");
        if (tv) {
            u.append("&air_date.gte=").append(from).append("&air_date.lte=").append(to);
            u.append("&sort_by=popularity.desc");
        } else {
            u.append("&primary_release_date.gte=").append(from).append("&primary_release_date.lte=").append(to);
            u.append("&sort_by=primary_release_date.desc");
        }
        if (!genreIds.isEmpty()) u.append("&with_genres=").append(enc(join(genreIds, matchAll ? "," : "|")));
        if (!keywordIds.isEmpty()) u.append("&with_keywords=").append(enc(join(keywordIds, "|")));
        u.append("&page=").append(page);
        return u.toString();
    }

    static String keywordUrl(Cfg c, String word) {
        return BASE + "/search/keyword?api_key=" + enc(c.key) + "&query=" + enc(word) + "&page=1";
    }

    static String configUrl(Cfg c) {
        return BASE + "/configuration?api_key=" + enc(c.key);
    }

    /** Resolves a typed word to TMDB keyword ids ("12|34"), using the cache first. Empty string if none. */
    static String keywordIds(Http http, Cfg c, KeywordCache cache, String word) throws IOException {
        String lw = word.toLowerCase(Locale.ROOT);
        String hit = cache.get(lw);
        if (hit != null) return hit;
        Map<String, Object> m = Json.map(Json.parse(http.get(keywordUrl(c, word))));
        List<String> exact = new ArrayList<String>();
        List<String> other = new ArrayList<String>();
        for (Object o : Json.list(m == null ? null : m.get("results"))) {
            Map<String, Object> k = Json.map(o);
            if (k == null) continue;
            String id = String.valueOf(Json.num(k, "id", 0));
            if (Json.str(k, "name", "").equalsIgnoreCase(word)) exact.add(id);
            else other.add(id);
        }
        List<String> use = !exact.isEmpty() ? exact : other.subList(0, Math.min(2, other.size()));
        String ids = join(use, "|");
        cache.put(lw, ids);
        return ids;
    }

    static List<Title> parse(String body, boolean tv, String ruleName, String windowFrom) {
        List<Title> out = new ArrayList<Title>();
        Map<String, Object> m = Json.map(Json.parse(body));
        for (Object o : Json.list(m == null ? null : m.get("results"))) {
            Map<String, Object> r = Json.map(o);
            if (r == null) continue;
            Title t = new Title();
            t.tv = tv;
            t.id = Json.num(r, "id", 0);
            t.title = Json.str(r, tv ? "name" : "title", Json.str(r, tv ? "original_name" : "original_title", ""));
            t.overview = Json.str(r, "overview", "");
            t.poster = Json.str(r, "poster_path", "");
            t.date = Json.str(r, tv ? "first_air_date" : "release_date", "");
            t.vote = Json.dbl(r, "vote_average", 0);
            t.rule = ruleName;
            for (Object g : Json.list(r.get("genre_ids"))) if (g instanceof Number) t.genres.add(((Number) g).intValue());
            t.newEpisodes = tv && !t.date.isEmpty() && t.date.compareTo(windowFrom) < 0;
            if (t.id != 0 && !t.title.isEmpty()) out.add(t);
        }
        return out;
    }

    static boolean textMatches(Title t, List<String> words) {
        String hay = (t.title + " " + t.overview).toLowerCase(Locale.ROOT);
        for (String w : words) if (hay.contains(w.toLowerCase(Locale.ROOT))) return true;
        return false;
    }

    /** Titles for one rule. Throws IOException for network/API problems (message like "HTTP 401"). */
    static List<Title> fetch(Http http, Cfg c, Rule rule, long now, KeywordCache cache) throws IOException {
        String to = day(now);
        String from = day(now - rule.days * 86_400_000L);
        List<Title> all = new ArrayList<Title>();
        HashSet<String> seen = new HashSet<String>();
        for (int kind = 0; kind < 2; kind++) {
            boolean tv = kind == 1;
            if (tv ? rule.type == Rule.MOVIES : rule.type == Rule.TV) continue;
            List<Integer> gids = Genres.ids(rule.genres, tv);
            if (!rule.genres.isEmpty() && gids.isEmpty()) continue; // chosen genres do not exist for this type
            List<String> kwIds = new ArrayList<String>();
            boolean text = rule.textMode && !rule.keywords.isEmpty();
            if (!rule.keywords.isEmpty() && !text) {
                for (String w : rule.keywords) {
                    String ids = keywordIds(http, c, cache, w);
                    if (!ids.isEmpty()) Collections.addAll(kwIds, ids.split("\\|"));
                }
                if (kwIds.isEmpty()) text = true; // no TMDB keyword found: fall back to text search
            }
            int pages = text ? Math.min(3, Math.max(1, c.pages + 1)) : Math.max(1, c.pages);
            for (int page = 1; page <= pages; page++) {
                String body = http.get(discoverUrl(c, tv, from, to, gids, rule.matchAll, text ? new ArrayList<String>() : kwIds, page));
                List<Title> got = parse(body, tv, rule.name, from);
                for (Title t : got) {
                    if (text && !textMatches(t, rule.keywords)) continue;
                    if (seen.add(t.key())) all.add(t);
                }
                Map<String, Object> m = Json.map(Json.parse(body));
                if (page >= Json.num(m, "total_pages", 1)) break;
            }
        }
        Collections.sort(all, new Comparator<Title>() {
            @Override
            public int compare(Title a, Title b) {
                int d = b.date.compareTo(a.date);
                return d != 0 ? d : Double.compare(b.vote, a.vote);
            }
        });
        return all;
    }
}
