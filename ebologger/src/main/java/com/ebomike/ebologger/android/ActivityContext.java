package com.ebomike.ebologger.android;

import android.app.Activity;

public class ActivityContext {
    public Activity getActivity() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : trace) {
            if (element.getClass().isInstance(Activity.class)) {
            }
        }

        return null;
    }
}
