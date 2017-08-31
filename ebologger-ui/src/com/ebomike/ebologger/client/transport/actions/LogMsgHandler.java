package com.ebomike.ebologger.client.transport.actions;

import com.ebomike.ebologger.client.model.*;
import com.ebomike.ebologger.client.transport.CommandContext;
import com.ebomike.ebologger.client.transport.CommandHandler;

import java.io.DataInputStream;
import java.io.IOException;

public class LogMsgHandler implements CommandHandler {
    @Override
    public void execute(CommandContext context, DataInputStream input, int version) throws IOException {
        long timestamp = input.readLong();
        int severity = input.read();
        int markerId = input.readInt();
        int tagId = input.readInt();
        String msg = input.readUTF();
        String tag = context.getModel().getTag(tagId);
        HostContext hostContext = context.getModel().getContext(input.readInt());
        HostObject object = context.getModel().getObject(input.readInt());
        HostThread thread = context.getModel().getThread(input.readInt());
        HostCallHierarchy hierarchy = context.getModel().getHierarchy(input.readInt());
        String marker = context.getModel().getMarker(markerId);

        System.out.println("Sev: " + severity + ", marker=" + marker + ", tagId=" + tagId + ", tag=" + tag +
            ", context=" + context + ", object=" + object + ", thread=" + thread + ", MSG: " + msg);

        LogMsg logMsg = new LogMsg(context.getModel(), timestamp, severity, marker, markerId, tagId, tag, msg,
                hostContext, thread, object, hierarchy);

        context.getUiInterface().addLog(logMsg);
        context.getModel().addLogMsg(logMsg);
    }
}
