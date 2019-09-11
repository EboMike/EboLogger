package com.ebomike.ebologger.android;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ebomike.ebologger.EboLogger;
import com.ebomike.ebologger.LogContextFactory;
import com.ebomike.ebologger.EboLogger.LogLevel;
import com.ebomike.ebologger.model.TrackedContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends a log message whenever a lifecycle change in an activity occurs (onCreate, onStart,
 * onResume, and its counterparts).
 */
@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActivityTracker implements Application.ActivityLifecycleCallbacks {
    @Nullable
    private static ActivityTracker instance = null;

    private final EboLogger logger = new EboLogger.Builder()
            .setTag("ACTIVITY")
            .setMinSeverity(LogLevel.DEBUG)
            .build();

    private final Map<Activity, TrackedContext> activities = new HashMap<>();

    private static final Object instanceMutex = new Object();

    /** Private constructor - create the instance with {@link #get}. */
    private ActivityTracker() {
    }

    /**
     * Retrurns the ActivityTracker singleton, or creates a new instance if there isn't one yet.
     *
     * @return The ActivityTracker singleton.
     */
    @AnyThread
    public static ActivityTracker get() {
        if (instance != null) {
            return instance;
        }

        synchronized(instanceMutex) {
            // We need to check one more time, this time within the synchronized block.
            if (instance == null) {
                instance = new ActivityTracker();
            }

            return instance;
        }
    }

    private TrackedContext getContext(Activity activity) {
        synchronized(activities) {
            TrackedContext context = activities.get(activity);

            if (context != null) {
                context = new TrackedContext(activity.toString());
            }

            return context;
        }
    }

    public void register(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Intent intent = activity.getIntent();
        log(activity, "Activity created",
                "onCreate with intent " + (intent != null ? intent.toString() : "(null)"));

        if (intent != null) {
            Bundle extras = intent.getExtras();

            if (extras != null) {
                for (String key : extras.keySet()) {
                    Object value = extras.get(key);
                    if (value != null) {
                        logger.verbose().log("Intent Extra: %s, type: %s, value: %s",
                                key, value.getClass().getSimpleName(), value.toString());
                    }
                }
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        log(activity, "Activity started", "onStart");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogContextFactory.create(getActivityName(activity));
        log(activity, "Activity resumes", "onResume");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogContextFactory.pop(getActivityName(activity));
        log(activity, "Activity paused", "onPause");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        log(activity, "Activity stopped", "onStop");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        log(activity, "Activity destroyed", "onDestroy - " + (activity.isFinishing() ?
            "finishing" : "not finishing"));
    }

    private void log(Activity activity, String marker, String action) {
        logger.info().marker(marker).tag("ACTIVITY").object(activity).log("%s: %s",
                action, activity.getClass().getSimpleName());
    }

    private static String getActivityName(Activity activity) {
        return activity.getClass().getSimpleName() + "@" + activity.hashCode();
    }
}
