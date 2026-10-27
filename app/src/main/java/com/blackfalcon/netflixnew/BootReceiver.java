package com.blackfalcon.netflixnew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Re-schedules the automatic check after the phone restarts. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Sched.apply(context);
        }
    }
}
