package com.ebomike.ebologger.android;

import com.ebomike.ebologger.Logger;
import com.ebomike.ebologger.model.FunctionalLogMessage;
import com.ebomike.ebologger.model.ProgramGraph;

public class AndroidLogMessage extends FunctionalLogMessage {
    public AndroidLogMessage(ProgramGraph graph, Logger.LogLevel severity, long timestamp) {
        super(graph, severity, timestamp);
    }

    @Override
    protected void sendMessage() {
        super.sendMessage();

        AndroidLogConnector.get().androidLog(getSeverity(), getTag(), getFormattedMessage(),
                getThrowable());
    }
}
