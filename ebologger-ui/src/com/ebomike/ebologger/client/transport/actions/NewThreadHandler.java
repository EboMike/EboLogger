package com.ebomike.ebologger.client.transport.actions;

import com.ebomike.ebologger.client.model.HostThread;
import com.ebomike.ebologger.client.transport.CommandContext;
import com.ebomike.ebologger.client.transport.CommandHandler;

import java.io.DataInputStream;
import java.io.IOException;

public class NewThreadHandler implements CommandHandler {
    @Override
    public void execute(CommandContext context, DataInputStream input, int version) throws IOException {
        int threadId = input.readInt();
        String name = input.readUTF();

        context.getModel().createThread(threadId, name);
    }
}
