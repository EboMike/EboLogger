package com.ebomike.ebologger.client.transport;

import com.ebomike.ebologger.client.transport.actions.*;
import com.sun.istack.internal.Nullable;

import java.util.HashMap;

public class Protocol {
    private HashMap<Integer, CommandHandler> commands = new HashMap<>();

    public Protocol() {
        commands.put(Commands.WELCOME, new WelcomeHandler());
        commands.put(Commands.LOGMSG, new LogMsgHandler());
        commands.put(Commands.NEW_THREAD, new NewThreadHandler());
        commands.put(Commands.NEW_OBJECT, new NewObjectHandler());
        commands.put(Commands.NEW_CONTEXT, new NewContextHandler());
        commands.put(Commands.NEW_MARKER, new NewMarkerHandler());
        commands.put(Commands.NEW_CLASS, new NewClassHandler());
        commands.put(Commands.NEW_METHOD, new NewMethodHandler());
        commands.put(Commands.NEW_HIERARCHY, new NewHierarchyHandler());
        commands.put(Commands.NEW_SOURCE_FILE, new NewSourceFileHandler());
        commands.put(Commands.NEW_TAG, new NewTagHandler());
    }

    @Nullable
    public CommandHandler getCommand(int command) {
        return commands.get(command);
    }
}
