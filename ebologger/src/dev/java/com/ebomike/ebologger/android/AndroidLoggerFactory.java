package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.Config;
import com.ebomike.ebologger.EboLogger;

public class AndroidLoggerFactory {
    private static final boolean ENABLED = true;

    public static EboLogger createLogger(String tag, @Nullable Object object,
                                         Config config) {
        if (ENABLED) {
            return new AndroidLogger(tag, object, config);
        } else {
            return new AndroidReleaseLogger(tag, config);
        }
    }
}