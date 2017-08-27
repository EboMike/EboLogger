package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.ebomike.ebologger.Logger;

public class AndroidLogConnector {
    // The singleton instance. Normally always used, except during testing.
    private static AndroidLogConnector instance = new AndroidLogConnector();

    public void androidLog(Logger.LogLevel severity, String tag, String msg,
                                  @Nullable Throwable e) {
        if (e == null) {
            switch (severity) {
                case DEBUG:
                    Log.d(tag, msg);
                    break;
                case VERBOSE:
                    Log.v(tag, msg);
                    break;
                case INFO:
                    Log.i(tag, msg);
                    break;
                case WARNING:
                    Log.w(tag, msg);
                    break;
                case ERROR:
                    Log.e(tag, msg);
                    break;
                case WTF:
                    Log.wtf(tag, msg);
                    break;
            }
        } else {
            switch (severity) {
                case DEBUG:
                    Log.d(tag, msg, e);
                    break;
                case VERBOSE:
                    Log.v(tag, msg, e);
                    break;
                case INFO:
                    Log.i(tag, msg, e);
                    break;
                case WARNING:
                    Log.w(tag, msg, e);
                    break;
                case ERROR:
                    Log.e(tag, msg, e);
                    break;
                case WTF:
                    Log.wtf(tag, msg, e);
                    break;
            }
        }
    }

    public static AndroidLogConnector get() {
        return instance;
    }

    @VisibleForTesting
    public static void setLogConnector(AndroidLogConnector connector) {
        instance = connector;
    }
}
