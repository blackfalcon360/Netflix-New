package com.blackfalcon.netflixnew;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

final class Store {
    private Store() {}

    private static SharedPreferences p(Context c) {
        return c.getApplicationContext().getSharedPreferences("netflixnew", Context.MODE_PRIVATE);
    }

    static String lang(Context c) { return p(c).getString("lang", "en"); }
    static void setLang(Context c, String v) { p(c).edit().putString("lang", v).apply(); }
    static int langIdx(Context c) {
        String l = lang(c);
        return "ur".equals(l) ? 1 : ("ar".equals(l) ? 2 : 0);
    }

    static boolean dark(Context c) { return p(c).getBoolean("dark", true); }
    static void setDark(Context c, boolean v) { p(c).edit().putBoolean("dark", v).apply(); }

    static String apiKey(Context c) { return p(c).getString("key", ""); }
    static void setApiKey(Context c, String v) { p(c).edit().putString("key", v.trim()).apply(); }

    static String region(Context c) { return p(c).getString("region", "PK"); }
    static void setRegion(Context c, String v) { p(c).edit().putString("region", v).apply(); }

    static int intervalHours(Context c) { return p(c).getInt("interval", 12); }
    static void setIntervalHours(Context c, int v) { p(c).edit().putInt("interval", v).apply(); }

    static boolean notify(Context c) { return p(c).getBoolean("notify", true); }
    static void setNotify(Context c, boolean v) { p(c).edit().putBoolean("notify", v).apply(); }

    static long lastCheck(Context c) { return p(c).getLong("last_check", 0); }
    static String lastError(Context c) { return p(c).getString("last_error", ""); }
    static void setLast(Context c, long t, String err) {
        p(c).edit().putLong("last_check", t).putString("last_error", err == null ? "" : err).apply();
    }

    // rules
    static List<Rule> rules(Context c) { return Rule.fromJson(p(c).getString("rules", "")); }
    static void saveRules(Context c, List<Rule> l) { p(c).edit().putString("rules", Rule.toJson(l)).apply(); }
    static boolean hasRulesKey(Context c) { return p(c).contains("rules"); }

    // seen titles per rule
    static LinkedHashSet<String> seen(Context c, String ruleId) {
        LinkedHashSet<String> s = new LinkedHashSet<String>();
        for (String x : p(c).getString("seen_" + ruleId, "").split("\n")) if (!x.isEmpty()) s.add(x);
        return s;
    }

    static void saveSeen(Context c, String ruleId, LinkedHashSet<String> s) {
        while (s.size() > 800) {
            Iterator<String> it = s.iterator();
            it.next();
            it.remove();
        }
        StringBuilder sb = new StringBuilder();
        for (String x : s) sb.append(x).append('\n');
        p(c).edit().putString("seen_" + ruleId, sb.toString()).putBoolean("init_" + ruleId, true).apply();
    }

    static boolean initialized(Context c, String ruleId) { return p(c).getBoolean("init_" + ruleId, false); }

    static void forgetRule(Context c, String ruleId) {
        p(c).edit().remove("seen_" + ruleId).remove("init_" + ruleId).apply();
    }

    // keyword cache: word -> "id|id"
    static Tmdb.KeywordCache keywordCache(final Context c) {
        return new Tmdb.KeywordCache() {
            @Override
            public String get(String word) {
                Map<String, Object> m = Json.map(parse(p(c).getString("kw", "{}")));
                return m == null ? null : Json.str(m, word, null);
            }

            @Override
            public void put(String word, String ids) {
                Map<String, Object> m = Json.map(parse(p(c).getString("kw", "{}")));
                LinkedHashMap<String, Object> n = new LinkedHashMap<String, Object>();
                if (m != null) n.putAll(m);
                n.put(word, ids);
                p(c).edit().putString("kw", Json.write(n)).apply();
            }
        };
    }

    private static Object parse(String s) {
        try {
            return Json.parse(s);
        } catch (RuntimeException e) {
            return null;
        }
    }

    static List<String> ruleIds(Context c) {
        List<String> l = new ArrayList<String>();
        for (Rule r : rules(c)) l.add(r.id);
        return l;
    }
}
