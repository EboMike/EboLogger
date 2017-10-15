package com.ebomike.ebologger;

import android.support.annotation.AnyThread;

import com.ebomike.ebologger.model.ReadableLogMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains the list of active {@link LogSender} objects. Whenever a log message is sent, it
 * will be processed by all LogSender objects that have been registered here.
 *
 * The LogSenderMgr is created on demand. At the time of its creation, it will set up all
 * @{link LogSender} objects that will be used to process messages.
 */
public class LogSenderMgr {
    private final List<LogSender> senders = new ArrayList<>();

    private static final LazySingleton<LogSenderMgr> instance = new LazySingleton<LogSenderMgr>() {
        @Override
        public LogSenderMgr create() {
            return new LogSenderMgr();
        }
    };

    public void addSender(LogSender sender) {
        senders.add(sender);
    }

    public void send(ReadableLogMessage message) {
        for (LogSender sender : senders) {
            sender.sendMessage(message);
        }
    }

    @AnyThread
    public static LogSenderMgr get() {
        return instance.get();
    }
}
