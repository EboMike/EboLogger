package com.ebomike.ebologger.client.model;

import com.ebomike.ebologger.client.transport.Commands;
import com.sun.istack.internal.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

public class LogMsg implements StreamSerializable {
    private final int id;

    private final long timestamp;

    private final int severity;

    private final int markerId;

    private final String marker;

    private final int tagId;

    private final String tag;

    private final String msg;

    private final HostContext context;

    private final HostThread thread;

    private final HostObject object;

    private final Model model;

    @Nullable
    private final HostCallHierarchy hierarchy;

    private static int nextId = 0;

    public LogMsg(Model model, long timestamp, int severity, String marker, int markerId, int tagId, String tag,
                  String msg, HostContext context, HostThread thread, HostObject object,
                  @Nullable HostCallHierarchy hierarchy) {
        this.model = model;
        this.timestamp = timestamp;
        this.severity = severity;
        this.marker = marker;
        this.markerId = markerId;
        this.context = context;
        this.tag = tag;
        this.tagId = tagId;
        this.msg = msg;
        this.thread = thread;
        this.object = object;
        this.hierarchy = hierarchy;
        this.id = ++nextId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSeverity() {
        return severity;
    }

    public String getMsg() {
        return msg;
    }

    public HostThread getThread() {
        return thread;
    }

    public HostObject getObject() {
        return object;
    }

    public HostCallHierarchy getHierarchy() {
        return hierarchy;
    }

    public int getTagId() {
        return tagId;
    }

    public String getTag() {
        return tag;
    }

    public HostContext getContext() {
        return context;
    }

    public String getMarker() {
        return marker;
    }

    public int getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.write(Commands.LOGMSG);
        out.writeLong(timestamp);
        out.write(severity);
        out.writeInt(markerId);
        out.writeInt(tagId);
        out.writeUTF(msg);
        out.writeInt(context != null ? context.getId() : 0);
        out.writeInt(object != null ? object.getId() : 0);
        out.writeInt(thread != null ? thread.getId() : 0);
        out.writeInt(hierarchy != null ? hierarchy.getId() : 0);
    }
}
