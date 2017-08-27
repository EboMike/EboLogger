package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;
import android.util.Log;

import com.ebomike.ebologger.Config;
import com.ebomike.ebologger.Logger;
import com.ebomike.ebologger.model.DummyLogMessage;
import com.ebomike.ebologger.model.LogMessage;

import java.util.UnknownFormatConversionException;

public class AndroidReleaseLogger extends Logger {
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
