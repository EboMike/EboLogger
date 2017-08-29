package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.EboLogger;

public class AndroidLoggerFactory {
    private static final boolean ENABLED = true;

    public static EboLogger createLogger(String tag, @Nullable Object object,
                                         EboLogger.LogLevel minSeverity) {
        if (ENABLED) {
            return new AndroidLogger(tag, object, minSeverity);
        } else {
            return new AndroidReleaseLogger(tag, minSeverity);
        }
    }
}