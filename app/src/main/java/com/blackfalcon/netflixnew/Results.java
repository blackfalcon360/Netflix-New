package com.blackfalcon.netflixnew;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The last list of titles, kept in a private file so it can be shown without internet. */
final class Results {
    private Results() {}

    private static final String FILE = "results.json";

    static final class Data {
        long time;
        List<Tmdb.Title> titles = new ArrayList<Tmdb.Title>();
    }

    static synchronized void save(Context c, long time, List<Tmdb.Title> titles) {
        try {
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("time", time);
            List<Object> l = new ArrayList<Object>();
            for (Tmdb.Title t : titles) l.add(t.toMap());
            m.put("titles", l);
            FileOutputStream out = c.openFileOutput(FILE, Context.MODE_PRIVATE);
            out.write(Json.write(m).getBytes("UTF-8"));
            out.close();
        } catch (Exception e) {
            // keep the old file
        }
    }

    static synchronized Data load(Context c) {
        Data d = new Data();
        try {
            FileInputStream in = c.openFileInput(FILE);
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) bo.write(buf, 0, n);
            in.close();
            Map<String, Object> m = Json.map(Json.parse(bo.toString("UTF-8")));
            d.time = Json.num(m, "time", 0);
            for (Object o : Json.list(m.get("titles"))) {
                Map<String, Object> tm = Json.map(o);
                if (tm != null) d.titles.add(Tmdb.Title.fromMap(tm));
            }
        } catch (Exception e) {
            // no results yet
        }
        return d;
    }

    static synchronized void clearFresh(Context c) {
        Data d = load(c);
        for (Tmdb.Title t : d.titles) t.fresh = false;
        save(c, d.time, d.titles);
    }
}
