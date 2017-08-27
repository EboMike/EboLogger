package com.ebomike.ebologger.android;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.FunctionalLogger;
import com.ebomike.ebologger.model.LogMessage;

class AndroidLogger extends FunctionalLogger {
    AndroidLogger(String tag, @Nullable Object object, LogLevel minSeverity) {
        super(tag, object, minSeverity);
    }

    @Override
    protected LogMessage createLogMessage(LogLevel severity) {
        // TODO: Return dummy message if this does not meet the threshold.
        return new AndroidLogMessage(getGraph(), severity, System.currentTimeMillis())
                .object(getObject()).tag(getTag());
    }
}
