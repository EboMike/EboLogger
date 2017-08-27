package com.ebomike.ebologger.client.transport.actions;

import com.ebomike.ebologger.client.model.HostCallHierarchy;
import com.ebomike.ebologger.client.transport.CommandContext;
import com.ebomike.ebologger.client.transport.CommandHandler;

import java.io.DataInputStream;
import java.io.IOException;

public class NewHierarchyHandler implements CommandHandler {
    @Override
    public void execute(CommandContext context, DataInputStream input, int version) throws IOException {
        short id = input.readShort();
        short classId = input.readShort();
        short methodId = input.readShort();
        short sourceFileId = input.readShort();
        short line = input.readShort();
        short parent = input.readShort();

        HostCallHierarchy hierarchy = new HostCallHierarchy(id, classId, methodId, sourceFileId, line,
                context.getModel().getHierarchy(parent));

        context.getModel().addHierarchy(id, hierarchy);
    }
}
