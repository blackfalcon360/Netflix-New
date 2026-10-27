package com.blackfalcon.netflixnew;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** One watch rule: which kind of titles, genres, keywords and how recent. */
final class Rule {
    static final int MOVIES = 0, TV = 1, BOTH = 2;

    String id = "";
    String name = "";
    int type = BOTH;
    List<String> genres = new ArrayList<String>();
    boolean matchAll = false;
    List<String> keywords = new ArrayList<String>();
    boolean textMode = false;
    int days = 30;
    boolean enabled = true;

    Map<String, Object> toMap() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", id);
        m.put("name", name);
        m.put("type", type);
        m.put("genres", new ArrayList<Object>(genres));
        m.put("all", matchAll);
        m.put("keywords", new ArrayList<Object>(keywords));
        m.put("text", textMode);
        m.put("days", days);
        m.put("on", enabled);
        return m;
    }

    static Rule fromMap(Map<String, Object> m) {
        Rule r = new Rule();
        r.id = Json.str(m, "id", "");
        r.name = Json.str(m, "name", "");
        r.type = (int) Json.num(m, "type", BOTH);
        for (Object o : Json.list(m.get("genres"))) if (o instanceof String) r.genres.add((String) o);
        r.matchAll = Json.bool(m, "all", false);
        for (Object o : Json.list(m.get("keywords"))) if (o instanceof String) r.keywords.add((String) o);
        r.textMode = Json.bool(m, "text", false);
        r.days = (int) Json.num(m, "days", 30);
        r.enabled = Json.bool(m, "on", true);
        return r;
    }

    static String toJson(List<Rule> rules) {
        List<Object> l = new ArrayList<Object>();
        for (Rule r : rules) l.add(r.toMap());
        return Json.write(l);
    }

    static List<Rule> fromJson(String s) {
        List<Rule> out = new ArrayList<Rule>();
        if (s == null || s.isEmpty()) return out;
        try {
            for (Object o : Json.list(Json.parse(s))) {
                Map<String, Object> m = Json.map(o);
                if (m != null) out.add(fromMap(m));
            }
        } catch (RuntimeException e) {
            // damaged data: start with no rules
        }
        return out;
    }

    /** Splits "zombie, time travel ,  " into clean keywords. */
    static List<String> splitKeywords(String s) {
        List<String> out = new ArrayList<String>();
        for (String p : s.split("[,\u060C\n]")) {
            String t = p.trim();
            if (!t.isEmpty() && !out.contains(t)) out.add(t);
        }
        return out;
    }
}
