package com.ebomike.ebologger;

import com.ebomike.ebologger.model.DummyLogMessage;
import com.ebomike.ebologger.model.LogMessage;

/** Implementation of {@link EboLogger} that does nothing. */
class NullLogger extends EboLogger {
    public NullLogger() {
        super(Config.getBaseConfig());
    }

    @Override
    protected LogMessage createLogMessage(LogLevel severity) {
        return DummyLogMessage.DUMMY_LOG_MESSAGE;
    }

    @Override
    protected String getTag() {
        return "NullLogger";
    }
}
