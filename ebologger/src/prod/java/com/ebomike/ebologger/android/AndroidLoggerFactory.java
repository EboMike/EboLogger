package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.Logger;

public class AndroidLoggerFactory {
    public static Logger createLogger(String tag, @Nullable Object object,
                                      Logger.LogLevel minSeverity) {
        return new AndroidReleaseLogger(tag, minSeverity);
    }
}
