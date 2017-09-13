package com.ebomike.ebologger.model;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.EboLogger.LogLevel;

public class FunctionalLogMessage implements LogMessage {
    private final ProgramGraph graph;

    private final LogLevel severity;

    private final long timestamp;

    private String formattedMessage;

    @Nullable
    private TrackedMarker marker;

    @Nullable
    private TrackedContext context;

    private String tag;

    private CallHierarchy callHierarchy;

    @Nullable
    private TrackedObject object;

    private TrackedThread thread;

    @Nullable
    private Throwable throwable;

    public FunctionalLogMessage(ProgramGraph graph, LogLevel severity, long timestamp) {
        this.graph = graph;
        this.severity = severity;
        this.timestamp = timestamp;

        if (!graph.isDisabled()) {
            callHierarchy = graph.getHierarchy(Thread.currentThread().getStackTrace());
            thread = graph.getThread(Thread.currentThread());
        }
    }

    private FunctionalLogMessage(FunctionalLogMessage other) {
        graph = other.graph;
        severity = other.severity;
        timestamp = other.timestamp;
        formattedMessage = other.formattedMessage;
        marker = other.marker;
        context = other.context;
        tag = other.tag;
        callHierarchy = other.callHierarchy;
        object = other.object;
        thread = other.thread;
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
        if (!graph.isDisabled()) {
            this.marker = graph.getMarker(marker);
        }
        return this;
    }

    @Override
    public LogMessage object(Object object) {
        if (!graph.isDisabled()) {
            this.object = graph.getObject(object);
        }
        return this;
    }

    @Override
    public void log(String message, Object... args) {
        formattedMessage = String.format(message, args);
        sendMessage();
    }

    protected void sendMessage() {
        if (graph.isDisabled()) {
            return;
        }

        graph.log(this);

        // Temporary - also add the throwable message itself.
        if (getThrowable() != null && getThrowable().getMessage() != null) {
            FunctionalLogMessage throwableMessage = new FunctionalLogMessage(this);
            throwableMessage.setFormattedMessage(getThrowable().getMessage());
            graph.log(throwableMessage);
        }
    }

    public LogLevel getSeverity() {
        return severity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Nullable
    public TrackedMarker getMarker() {
        return marker;
    }

    @Nullable
    public TrackedContext getContext() {
        return context;
    }

    public String getTag() {
        return tag;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    private void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public CallHierarchy getCallHierarchy() {
        return callHierarchy;
    }

    @Nullable
    public TrackedObject getObject() {
        return object;
    }

    public TrackedThread getThread() {
        return thread;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
