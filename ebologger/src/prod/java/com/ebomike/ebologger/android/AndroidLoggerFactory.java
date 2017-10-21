package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.Config;
import com.ebomike.ebologger.EboLogger;

public class AndroidLoggerFactory {
    public static EboLogger createLogger(String tag, @Nullable Object object,
                                      Config config) {
        return new AndroidReleaseLogger(tag, config);
    }
}
