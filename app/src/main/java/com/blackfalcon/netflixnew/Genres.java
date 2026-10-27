package com.blackfalcon.netflixnew;

import java.util.ArrayList;
import java.util.List;

/** Genres with their TMDB ids for movies and TV (0 = not available for that type) and names in 3 languages. */
final class Genres {
    private Genres() {}

    // key, movieId, tvId, en, ur, ar
    static final String[][] G = {
            {"action", "28", "10759", "Action", "ایکشن", "أكشن"},
            {"adventure", "12", "10759", "Adventure", "ایڈونچر", "مغامرة"},
            {"animation", "16", "16", "Animation", "اینی میشن", "رسوم متحركة"},
            {"comedy", "35", "35", "Comedy", "کامیڈی", "كوميديا"},
            {"crime", "80", "80", "Crime", "جرائم", "جريمة"},
            {"documentary", "99", "99", "Documentary", "دستاویزی", "وثائقي"},
            {"drama", "18", "18", "Drama", "ڈرامہ", "دراما"},
            {"family", "10751", "10751", "Family", "فیملی", "عائلي"},
            {"fantasy", "14", "10765", "Fantasy", "فنتاسی", "فانتازيا"},
            {"history", "36", "0", "History", "تاریخی", "تاريخ"},
            {"horror", "27", "0", "Horror", "ہارر", "رعب"},
            {"music", "10402", "0", "Music", "موسیقی", "موسيقى"},
            {"mystery", "9648", "9648", "Mystery", "پراسرار", "غموض"},
            {"romance", "10749", "0", "Romance", "رومانوی", "رومانسي"},
            {"scifi", "878", "10765", "Science Fiction", "سائنس فکشن", "خيال علمي"},
            {"thriller", "53", "0", "Thriller", "تھرلر", "إثارة"},
            {"war", "10752", "10768", "War", "جنگی", "حرب"},
            {"western", "37", "37", "Western", "ویسٹرن", "غربي"},
            {"kids", "0", "10762", "Kids", "بچوں کے لیے", "أطفال"},
            {"reality", "0", "10764", "Reality", "ریئلٹی", "واقع"},
            {"talk", "0", "10767", "Talk show", "ٹاک شو", "برنامج حواري"},
            {"news", "0", "10763", "News", "خبریں", "أخبار"},
            {"soap", "0", "10766", "Soap", "سوپ", "مسلسل درامي"},
    };

    // display names of TMDB's combined TV genres
    private static final String[][] TV_COMBINED = {
            {"10759", "Action & Adventure", "ایکشن اور ایڈونچر", "أكشن ومغامرة"},
            {"10765", "Sci-Fi & Fantasy", "سائنس فکشن اور فنتاسی", "خيال علمي وفانتازيا"},
            {"10768", "War & Politics", "جنگ اور سیاست", "حرب وسياسة"},
    };

    static List<String> keys() {
        List<String> l = new ArrayList<String>();
        for (String[] g : G) l.add(g[0]);
        return l;
    }

    static String name(String key, int lang) {
        for (String[] g : G) if (g[0].equals(key)) return g[3 + lang];
        return key;
    }

    /** TMDB ids for the chosen genre keys; keys that do not exist for that type are dropped; no duplicates. */
    static List<Integer> ids(List<String> keys, boolean tv) {
        List<Integer> out = new ArrayList<Integer>();
        for (String k : keys) {
            for (String[] g : G) {
                if (!g[0].equals(k)) continue;
                int id = Integer.parseInt(g[tv ? 2 : 1]);
                if (id != 0 && !out.contains(id)) out.add(id);
            }
        }
        return out;
    }

    /** Name for a genre id returned by TMDB. */
    static String nameById(int id, boolean tv, int lang) {
        if (tv) {
            for (String[] c : TV_COMBINED) if (Integer.parseInt(c[0]) == id) return c[1 + lang];
        }
        for (String[] g : G) {
            if (Integer.parseInt(g[tv ? 2 : 1]) == id) return g[3 + lang];
        }
        return "";
    }
}
