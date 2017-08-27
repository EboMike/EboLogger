package com.ebomike.ebologger.model;

import android.support.annotation.AnyThread;

public interface LogMessage {
    LogMessage exception(Throwable throwable);

    LogMessage tag(String tag);

    LogMessage object(Object object);

    LogMessage marker(String marker);

    /**
     * Format the string and sends it off. That could be with the native Android logcat system,
     * and/or over the wire to the host.
     */
    @AnyThread
    void log(String message, Object... args);
}
