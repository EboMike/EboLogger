package com.ebomike.ebologger.client.model;

import com.ebomike.ebologger.client.transport.Commands;
import com.sun.istack.internal.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Model {
    private Map<Integer, HostThread> threads = new HashMap<>();

    private Map<Integer, HostObject> objects = new HashMap<>();

    private Map<Integer, HostContext> contexts = new HashMap<>();

    private Map<Integer, HostStackTrace> stackTraces = new HashMap<>();

    private Map<Integer, HostCallHierarchy> hierarchies = new HashMap<>();

    private Map<Integer, String> tags = new HashMap<>();

    private Map<Integer, String> markers = new HashMap<>();

    private Map<Integer, String> classes = new HashMap<>();

    private Map<Integer, String> methods = new HashMap<>();

    private Map<Integer, String> sourceFiles = new HashMap<>();

    private List<LogMsg> logMsgs = new ArrayList<>();

    public void createThread(int threadId, String name) {
        HostThread thread = new HostThread(threadId, name);
        threads.put(threadId, thread);
    }

    public void createObject(int objectId, String name) {
        HostObject object = new HostObject(objectId, name);
        objects.put(objectId, object);
    }

    public void createContext(int contextId, String name) {
        HostContext context = new HostContext(contextId, name);
        contexts.put(contextId, context);
    }

    public void addClass(int classId, String name) {
        classes.put(classId, name);
    }

    public void addMethod(int methodId, String name) {
        methods.put(methodId, name);
    }

    public void addSourceFile(int sourceFileId, String name) {
        sourceFiles.put(sourceFileId, name);
    }

    public void addTag(int tagId, String tag) {
        tags.put(tagId, tag);
    }

    public void addMarker(int tagId, String tag) {
        markers.put(tagId, tag);
    }

    public void addHierarchy(int hierarchyId, HostCallHierarchy hierarchy) {
        hierarchies.put(hierarchyId, hierarchy);
    }

    public HostThread getThread(int threadId) {
        return threads.get(threadId);
    }

    public Collection<HostThread> getThreads() {
        return threads.values();
    }

    @Nullable
    public HostObject getObject(int objectId) {
        return objects.get(objectId);
    }

    @Nullable
    public HostContext getContext(int contextId) {
        return contexts.get(contextId);
    }

    @Nullable
    public HostCallHierarchy getHierarchy(int hierarchyId) {
        return hierarchies.get(hierarchyId);
    }

    public String getMarker(int markerId) {
        String result = markers.get(markerId);

        return result != null ? result : "";
    }

    public String getTag(int tagId) {
        return tags.get(tagId);
    }

    @Nullable
    public String getClassName(int classId) {
        return classes.get(classId);
    }

    @Nullable
    public String getMethodName(int methodId) {
        return methods.get(methodId);
    }

    @Nullable
    public String getSourceFile(int sourceFileId) {
        return methods.get(sourceFileId);
    }

    public void addLogMsg(LogMsg logMsg) {
        logMsgs.add(logMsg);
    }

    public void save(OutputStream outputStream) throws IOException {
        DataOutputStream out = new DataOutputStream(outputStream);
        out.write(1);
        out.write(Commands.WELCOME);
        out.writeInt(1);

        for (HostThread thread : threads.values()) {
            thread.save(out);
        }

        for (HostObject object : objects.values()) {
            object.save(out);
        }

        for (HostContext context : contexts.values()) {
            context.save(out);
        }

        for (Map.Entry<Integer, String> marker : markers.entrySet()) {
            out.write(Commands.NEW_MARKER);
            out.writeInt(marker.getKey());
            out.writeUTF(marker.getValue());
        }

        for (Map.Entry<Integer, String> tag : tags.entrySet()) {
            out.write(Commands.NEW_TAG);
            out.writeInt(tag.getKey());
            out.writeUTF(tag.getValue());
        }

        for (Map.Entry<Integer, String> clazz : classes.entrySet()) {
            out.write(Commands.NEW_CLASS);
            out.writeShort(clazz.getKey());
            out.writeUTF(clazz.getValue());
        }

        for (Map.Entry<Integer, String> method : methods.entrySet()) {
            out.write(Commands.NEW_METHOD);
            out.writeShort(method.getKey());
            out.writeUTF(method.getValue());
        }

        for (Map.Entry<Integer, String> sourceFile : sourceFiles.entrySet()) {
            out.write(Commands.NEW_SOURCE_FILE);
            out.writeShort(sourceFile.getKey());
            out.writeUTF(sourceFile.getValue());
        }

        for (Map.Entry<Integer, HostCallHierarchy> hierarchy : hierarchies.entrySet()) {
            out.write(Commands.NEW_HIERARCHY);
            out.writeShort(hierarchy.getKey());
            out.writeShort(hierarchy.getValue().getClassId());
            out.writeShort(hierarchy.getValue().getMethodId());
            out.writeShort(hierarchy.getValue().getSourceFileId());
            out.writeShort(hierarchy.getValue().getLine());
            out.writeShort(hierarchy.getValue().getParent() == null ? 0 : hierarchy.getValue().getParent().getId());
        }

        for (LogMsg msg : logMsgs) {
            msg.save(out);
        }

        out.write(Commands.CMD_TERMINUS);
    }

    public List<LogMsg> getLogMsgs() {
        return logMsgs;
    }
}
