package com.blackfalcon.netflixnew;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Poster pictures from image.tmdb.org with a small memory cache. */
final class Posters {
    private Posters() {}

    private static final LruCache<String, Bitmap> CACHE = new LruCache<String, Bitmap>(16 * 1024 * 1024) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };
    private static final ExecutorService POOL = Executors.newFixedThreadPool(3);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    static void load(final String path, final ImageView iv) {
        iv.setTag(path);
        iv.setImageDrawable(null);
        if (path == null || path.isEmpty()) return;
        Bitmap hit = CACHE.get(path);
        if (hit != null) {
            iv.setImageBitmap(hit);
            return;
        }
        POOL.execute(() -> {
            final Bitmap bm = download(path);
            if (bm == null) return;
            CACHE.put(path, bm);
            MAIN.post(() -> {
                if (path.equals(iv.getTag())) iv.setImageBitmap(bm);
            });
        });
    }

    private static Bitmap download(String path) {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL("https://image.tmdb.org/t/p/w185" + path).openConnection();
            c.setConnectTimeout(15000);
            c.setReadTimeout(20000);
            if (c.getResponseCode() != 200) return null;
            InputStream in = c.getInputStream();
            Bitmap b = BitmapFactory.decodeStream(in);
            in.close();
            return b;
        } catch (Exception e) {
            return null;
        } finally {
            if (c != null) c.disconnect();
        }
    }
}
