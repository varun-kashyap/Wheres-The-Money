package com.varunkashyap.wheresthemoney;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by varun on 25-11-2016.
 */

public class WTMApplication extends Application {
    private Tracker mTracker;

    public void startTracking() {
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = ga.newTracker(R.xml.global_tracker);
            ga.enableAutoActivityReports(this);
        }

    }

    public Tracker getTracker() {
        startTracking();
        return mTracker;
    }

}
