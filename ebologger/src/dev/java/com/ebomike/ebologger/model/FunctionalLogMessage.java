package com.ebomike.ebologger.model;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.EboLogger.LogLevel;
import com.ebomike.ebologger.GlobalConfig;
import com.ebomike.ebologger.LogSender;
import com.ebomike.ebologger.LogSenderMgr;

public class FunctionalLogMessage implements LogMessage, ReadableLogMessage {
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

        GlobalConfig config = GlobalConfig.get();

        for (LogSender sender : config.getLogSenders()) {
            sender.sendMessage(this);
        }
    }

    @Override
    public LogLevel getSeverity() {
        return severity;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    @Nullable
    public TrackedMarker getMarker() {
        return marker;
    }

    @Override
    @Nullable
    public TrackedContext getContext() {
        return context;
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
        return callHierarchy;
    }

    @Override
    @Nullable
    public TrackedObject getObject() {
        return object;
    }

    @Override
    public TrackedThread getThread() {
        return thread;
    }

    @Override
    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
