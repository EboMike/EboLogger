package com.ebomike.ebologger.model;

import androidx.annotation.AnyThread;

/**
 * A builder for a logging message. It has various methods to add more information to it, and
 * finally @{link #log} to finalize the message and send it off.
 *
 * There are various implementations, some are complete null ops, others ignore expensive
 * operations.
 */
public interface LogMessage {
    /** Add a {@link Throwable} that is associated with this error. */
    LogMessage exception(Throwable throwable);

    /** Add a tag for this message. */
    LogMessage tag(String tag);

    /**
     * Add an arbitrary object that is associated with this message. This will only be used if this
     * log message is inspected with an external tool.
     **/
    LogMessage object(Object object);

    /**
     * Add a marker for this log message to indicate a noteworthy event. This will only be used if
     * this log message is inspected with an external tool.
     */
    LogMessage marker(String marker);

    /**
     * Format the string and sends it off. That could be with the native Android logcat system,
     * and/or over the wire to the host.
     */
    @AnyThread
    void log(String message, Object... args);
}
