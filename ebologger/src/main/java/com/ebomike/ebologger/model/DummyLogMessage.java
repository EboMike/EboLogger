package com.ebomike.ebologger.model;

public class DummyLogMessage implements LogMessage {
    /** A dummy log message is immutable, so we only need one instance. */
    public static final DummyLogMessage DUMMY_LOG_MESSAGE = new DummyLogMessage();

    @Override
    public LogMessage object(Object object) {
        return this;
    }

    @Override
    public LogMessage marker(String marker) {
        return this;
    }

    @Override
    public final LogMessage exception(Throwable throwable) {
        return this;
    }

    @Override
    public LogMessage tag(String tag) {
        return this;
    }

    @Override
    public final void log(String message, Object... args) {
    }
}
