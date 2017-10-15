package com.ebomike.ebologgerdemo;

import android.app.Application;

import com.ebomike.ebologger.GlobalConfig;
import com.ebomike.ebologger.android.ActivityTracker;
import com.ebomike.ebologger.android.CrashHandler;

public class EboLoggerDemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // This is a good place to initialize the logger. It can also be done anywhere else, like
        // an activity, but Application.create() runs only once, and before any Activity is
        // created.

        // Creating a global configuration is completely optional. We're doing it here for
        // demonstration purposes. By default, we have an Android log writer (all environments),
        // and a stream writer to send data to the UI (dev only).
        new GlobalConfig.Builder()
//                .addSender()
                .build()
                .activate();

        // Initializing the crash handler will sends a log to the UI if the application crashes
        // entirely.
        CrashHandler.create().install();

        // The activity tracker will automatically send logs whenever an activity lifecycle event
        // happens, such as onCreate(), onResume(), etc.
        ActivityTracker.get().register(this);
    }
}
