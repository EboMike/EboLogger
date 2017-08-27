package com.ebomike.ebologger.client.model;

import com.sun.istack.internal.Nullable;

public class HostCallHierarchy {
    private final short id;

    private final short classId;

    private final short methodId;

    private final short sourceFileId;

    private final short line;

    @Nullable
    private HostCallHierarchy parent;

    public HostCallHierarchy(short id, short classId, short methodId, short sourceFileId, short line,
                             @Nullable HostCallHierarchy parent) {
        this.id = id;
        this.classId = classId;
        this.methodId = methodId;
        this.sourceFileId = sourceFileId;
        this.line = line;
        this.parent = parent;
    }

    public short getId() {
        return id;
    }

    public short getClassId() {
        return classId;
    }

    public short getMethodId() {
        return methodId;
    }

    public short getSourceFileId() {
        return sourceFileId;
    }

    public short getLine() {
        return line;
    }

    public HostCallHierarchy getParent() {
        return parent;
    }
}
