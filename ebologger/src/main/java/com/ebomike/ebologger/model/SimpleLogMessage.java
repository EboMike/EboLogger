package com.ebomike.ebologger.model;

import androidx.annotation.Nullable;

import com.ebomike.ebologger.EboLogger;
import com.ebomike.ebologger.EboLogger.LogLevel;

/**
 * A more lightweight implementation of {@link LogMessage} that ignores more heavyweight components
 * such as object, marker, or call hierarchy.
 */
public class SimpleLogMessage implements LogMessage, ReadableLogMessage {
    private final EboLogger logger;

    private final LogLevel severity;

    private String formattedMessage;

    private String tag;

    @Nullable
    private Throwable throwable;

    public SimpleLogMessage(EboLogger logger, String tag, LogLevel severity) {
        this.logger = logger;
        this.severity = severity;
        this.tag = tag;
    }

    private SimpleLogMessage(SimpleLogMessage other) {
        logger = other.logger;
        severity = other.severity;
        formattedMessage = other.formattedMessage;
        tag = other.tag;
        throwable = other.throwable;
    }

    @Override
    public LogMessage exception(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override
    public LogMessage tag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public LogMessage marker(String marker) {
        return this;
    }

    @Override
    public LogMessage object(Object object) {
        return this;
    }

    @Override
    public void log(String message, Object... args) {
        formattedMessage = String.format(message, args);

        logger.sendMessage(this);
    }

    @Override
    public LogLevel getSeverity() {
        return severity;
    }

    @Override
    public long getTimestamp() {
        return 0L;
    }

    @Override
    @Nullable
    public TrackedMarker getMarker() {
        return null;
    }

    @Override
    @Nullable
    public TrackedContext getContext() {
        return null;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getFormattedMessage() {
        return formattedMessage;
    }

    private void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    @Override
    public CallHierarchy getCallHierarchy() {
        return null;
    }

    @Override
    @Nullable
    public TrackedObject getObject() {
        return null;
    }

    @Override
    public TrackedThread getThread() {
        return null;
    }

    @Override
    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
