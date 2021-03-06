package com.ebomike.ebologger.android;

import com.ebomike.ebologger.LogSender;
import com.ebomike.ebologger.model.ReadableLogMessage;

/**
 * Implementation of @{link LogSender} that will send the message to the Android log system.
 */
public class AndroidLogSender extends LogSender {
    public AndroidLogSender(int senderId) {
        super(senderId);
    }

    @Override
    public void sendMessage(ReadableLogMessage message) {
        AndroidLogConnector.get().androidLog(message.getSeverity(), message.getTag(),
                message.getFormattedMessage(), message.getThrowable());
    }
}
