package com.ebomike.ebologger;

import com.ebomike.ebologger.model.ReadableLogMessage;

import java.io.IOException;

public class AsciiLogSender extends LogSender {
    private static final AsciiLogSender DUMMY = new AsciiLogSender(0);

    public AsciiLogSender(int senderId) {
        super(senderId);
    }

    @Override
    public void sendMessage(ReadableLogMessage message) {

    }

    public static class Builder {
        public Builder filename(String dummy) throws IOException {
            return this;
        }

        public AsciiLogSender build() {
            return DUMMY;
        }
    }
}
