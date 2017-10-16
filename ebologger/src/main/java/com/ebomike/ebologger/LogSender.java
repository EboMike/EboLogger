package com.ebomike.ebologger;

import com.ebomike.ebologger.model.ReadableLogMessage;

/**
 * Sends a @{link LogMessage}. This could be the TTY, a file, the Android log system, or across
 * the network to a log server. LogSenders need to be registered with
 * {@link GlobalConfig.Builder#addSender}. Every message being logged will be processed by every
 * active LogSender.
 */
public abstract class LogSender {
    /**
     * ID to identify this sender, mostly for use in {@link Config}. Does not have to be unique.
     */
    private final int senderId;

    public LogSender(int senderId) {
        this.senderId = senderId;
    }

    public int getSenderId() {
        return senderId;
    }

    /** Sends a message. */
    public abstract void sendMessage(ReadableLogMessage message);
}
