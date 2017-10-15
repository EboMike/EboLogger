package com.ebomike.ebologger.model;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.EboLogger;

/**
 * Provides read access to the data of a log message. This is separate from LogMessage because we
 * don't want to expose it to users. The data may not be available based on the environment.
 */
public interface ReadableLogMessage {
    EboLogger.LogLevel getSeverity();

    long getTimestamp();

    @Nullable
    TrackedMarker getMarker();

    @Nullable
    TrackedContext getContext();

    String getTag();

    String getFormattedMessage();

    CallHierarchy getCallHierarchy();

    @Nullable
    TrackedObject getObject();

    TrackedThread getThread();

    @Nullable
    Throwable getThrowable();
}
