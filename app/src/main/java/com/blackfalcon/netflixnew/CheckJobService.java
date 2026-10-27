package com.blackfalcon.netflixnew;

import android.app.job.JobParameters;
import android.app.job.JobService;

/** Runs the periodic check. */
public class CheckJobService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters params) {
        new Thread(() -> {
            try {
                Checker.run(CheckJobService.this, true);
            } finally {
                jobFinished(params, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
