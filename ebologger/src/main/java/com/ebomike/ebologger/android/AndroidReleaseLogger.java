package com.ebomike.ebologger.android;

import com.ebomike.ebologger.Config;
import com.ebomike.ebologger.EboLogger;
import com.ebomike.ebologger.model.DummyLogMessage;
import com.ebomike.ebologger.model.LogMessage;

public class AndroidReleaseLogger extends EboLogger {
    private final String tag;

    private final Config config;

    public AndroidReleaseLogger(String tag, LogLevel minSeverity) {
        this.tag = tag;
        //this.minSeverity = minSeverity;
        config = new Config(minSeverity, minSeverity);
    }

    @Override
    protected LogMessage createLogMessage(LogLevel severity) {
        return new DummyLogMessage();
    }

    @Override
    public String getTag() {
        return tag;
    }
}
