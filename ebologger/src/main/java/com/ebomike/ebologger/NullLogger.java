package com.ebomike.ebologger;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.model.LogMessage;

class NullLogger extends Logger {
    @Override
    protected LogMessage createLogMessage(LogLevel severity) {
        return null;
    }

    @Override
    protected String getTag() {
        return "NullLogger";
    }
}
