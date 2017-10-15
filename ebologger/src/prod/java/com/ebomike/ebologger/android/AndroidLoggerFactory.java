package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.EboLogger;

public class AndroidLoggerFactory {
    public static EboLogger createLogger(String tag, @Nullable Object object,
                                      EboLogger.LogLevel minSeverity) {
        return new AndroidReleaseLogger(tag, minSeverity);
    }
}
