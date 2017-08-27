package com.ebomike.ebologger.client.transport.actions;

import com.ebomike.ebologger.client.model.HostThread;
import com.ebomike.ebologger.client.transport.CommandContext;
import com.ebomike.ebologger.client.transport.CommandHandler;

import java.io.DataInputStream;
import java.io.IOException;

public class NewContextHandler implements CommandHandler {
    @Override
    public void execute(CommandContext context, DataInputStream input, int version) throws IOException {
        int contextId = input.readInt();
        String name = input.readUTF();

        context.getModel().createContext(contextId, name);
    }
}
