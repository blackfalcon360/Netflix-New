package com.blackfalcon.netflixnew;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/** Runs all enabled rules, tracks which titles are new, saves results and notifies. */
final class Checker {
    private Checker() {}

    /** Returns null on success, otherwise an error code: nokey, norule, HTTP 401, HTTP 429, net. */
    static synchronized String run(Context c, boolean notify) {
        Context app = c.getApplicationContext();
        Tmdb.Cfg cfg = new Tmdb.Cfg();
        cfg.key = Store.apiKey(app);
        cfg.region = Store.region(app);
        if (cfg.key.isEmpty()) return "nokey";
        List<Rule> rules = Store.rules(app);
        List<Rule> active = new ArrayList<Rule>();
        for (Rule r : rules) if (r.enabled) active.add(r);
        if (active.isEmpty()) return "norule";

        long now = System.currentTimeMillis();
        UrlHttp http = new UrlHttp();
        Tmdb.KeywordCache kw = Store.keywordCache(app);
        List<Tmdb.Title> all = new ArrayList<Tmdb.Title>();
        int fresh = 0;
        StringBuilder names = new StringBuilder();
        for (Rule r : active) {
            List<Tmdb.Title> got;
            try {
                got = Tmdb.fetch(http, cfg, r, now, kw);
            } catch (IOException e) {
                String m = e.getMessage() == null ? "" : e.getMessage();
                String code = m.startsWith("HTTP 401") || m.startsWith("HTTP 403") ? "HTTP 401"
                        : (m.startsWith("HTTP 429") ? "HTTP 429" : "net");
                Store.setLast(app, now, code);
                return code;
            } catch (RuntimeException e) {
                Store.setLast(app, now, "net");
                return "net";
            }
            LinkedHashSet<String> seen = Store.seen(app, r.id);
            boolean baseline = !Store.initialized(app, r.id);
            int n = Seen.mark(seen, got, baseline);
            Store.saveSeen(app, r.id, seen);
            fresh += n;
            for (Tmdb.Title t : got) {
                if (t.fresh && names.length() < 220) {
                    if (names.length() > 0) names.append(", ");
                    names.append(t.title);
                }
            }
            all.addAll(got);
        }
        Results.save(app, now, all);
        Store.setLast(app, now, "");
        if (notify && fresh > 0) Notifier.show(app, fresh, names.toString());
        return null;
    }
}
