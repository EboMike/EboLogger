package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.Logger;

public class AndroidLoggerFactory {
    private static final boolean ENABLED = true;

    public static Logger createLogger(String tag, @Nullable Object object,
                                      Logger.LogLevel minSeverity) {
        if (ENABLED) {
            return new AndroidLogger(tag, object, minSeverity);
        } else {
            return new AndroidReleaseLogger(tag, minSeverity);
        }
    }
}