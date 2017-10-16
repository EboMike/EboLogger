package com.ebomike.ebologger;

import com.ebomike.ebologger.model.LogMessage;

class NullLogger extends EboLogger {    public NullLogger() {
        super(Config.getBaseConfig());
    }

    @Override
    protected LogMessage createLogMessage(LogLevel severity) {
        return null;
    }

    @Override
    protected String getTag() {
        return "NullLogger";
    }
}
