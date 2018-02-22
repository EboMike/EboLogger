package com.ebomike.ebologger;

import com.ebomike.ebologger.model.FunctionalLogMessage;
import com.ebomike.ebologger.model.ProgramGraph;
import com.ebomike.ebologger.model.ReadableLogMessage;

/**
 * Implementation of @{link LogSender} that sends the log message through a stream where it can be
 * consumed by a log server. The stream is unidirectional and could be a network connection or a
 * file.
 */
public class LogStreamSender extends LogSender {
    private final ProgramGraph graph;

    public LogStreamSender(int senderId, ProgramGraph graph) {
        super(senderId);
        this.graph = graph;
    }

    @Override
    public void sendMessage(ReadableLogMessage message) {
        if (graph.isDisabled()) {
            return;
        }

        graph.log(message);

        // Temporary - also add the throwable message itself.
        if (message.getThrowable() != null && message.getThrowable().getMessage() != null) {
            // TODO:
//            FunctionalLogMessage throwableMessage =
//                    new FunctionalLogMessage(message, message.getThrowable().getMessage());
//            throwableMessage.setFormattedMessage(message.getThrowable().getMessage());
//            graph.log(throwableMessage);
        }
    }
}
