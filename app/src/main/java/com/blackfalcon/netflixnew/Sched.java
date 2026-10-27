package com.blackfalcon.netflixnew;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

/** Periodic background check through Android's job scheduler (survives reboot). */
final class Sched {
    private Sched() {}

    static final int JOB_ID = 4711;

    static void apply(Context c) {
        try {
            JobScheduler js = (JobScheduler) c.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (js == null) return;
            int hours = Store.intervalHours(c);
            if (hours <= 0 || Store.apiKey(c).isEmpty()) {
                js.cancel(JOB_ID);
                return;
            }
            long ms = hours * 3600_000L;
            JobInfo pending = js.getPendingJob(JOB_ID);
            if (pending != null && pending.getIntervalMillis() == ms) return;
            JobInfo ji = new JobInfo.Builder(JOB_ID, new ComponentName(c, CheckJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(ms)
                    .setPersisted(true)
                    .build();
            js.schedule(ji);
        } catch (Exception e) {
            // scheduling not possible right now
        }
    }
}
