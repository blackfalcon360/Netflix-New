package com.blackfalcon.netflixnew;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Locale;

/** Notification about new titles. SDK-specific code lives in small annotated methods. */
final class Notifier {
    private Notifier() {}

    private static final String CHANNEL = "new_titles";

    static boolean allowed(Context c) {
        if (Build.VERSION.SDK_INT >= 33) {
            return c.checkSelfPermission("android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    static void show(Context c, int count, String text) {
        if (!Store.notify(c) || !allowed(c)) return;
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            makeChannel(nm, c);
            b = newBuilder(c);
        } else {
            b = oldBuilder(c);
        }
        Intent open = new Intent(c, MainActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, 0, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        b.setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(String.format(Locale.US, S.t(c, "nt_title"), count))
                .setContentText(text)
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setContentIntent(pi)
                .setAutoCancel(true);
        nm.notify(1, b.build());
    }

    @TargetApi(26)
    private static void makeChannel(NotificationManager nm, Context c) {
        NotificationChannel ch = new NotificationChannel(CHANNEL, S.t(c, "app_name"), NotificationManager.IMPORTANCE_DEFAULT);
        nm.createNotificationChannel(ch);
    }

    @TargetApi(26)
    private static Notification.Builder newBuilder(Context c) {
        return new Notification.Builder(c, CHANNEL);
    }

    @SuppressWarnings("deprecation")
    private static Notification.Builder oldBuilder(Context c) {
        return new Notification.Builder(c);
    }
}
