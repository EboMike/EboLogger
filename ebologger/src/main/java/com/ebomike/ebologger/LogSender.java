package com.ebomike.ebologger;

import com.ebomike.ebologger.model.ReadableLogMessage;

/**
 * Sends a @{link LogMessage}. This could be the TTY, a file, the Android log system, or across
 * the network to a log server. LogSenders need to be registered with
 * {@link LogSenderMgr#addSender}. Every message being logged will be processed by every active
 * LogSender.
 */
public interface LogSender {
    /** Sends a message. */
    void sendMessage(ReadableLogMessage message);
}
