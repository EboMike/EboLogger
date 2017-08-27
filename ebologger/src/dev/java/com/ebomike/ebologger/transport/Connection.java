package com.ebomike.ebologger.transport;

import android.util.Log;

import com.ebomike.ebologger.model.CallHierarchy;
import com.ebomike.ebologger.model.FunctionalLogMessage;
import com.ebomike.ebologger.model.ProgramGraph;
import com.ebomike.ebologger.model.TrackedContext;
import com.ebomike.ebologger.model.TrackedMarker;
import com.ebomike.ebologger.model.TrackedObject;
import com.ebomike.ebologger.model.TrackedThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The connection between the logger and the output stream, which could be a file or a network
 * connection to the app.
 *
 * After creation, {@link #connect} will start a new thread and try to establish a new cconnection.
 */
public class Connection {
    private static final String TAG = "EboLogger";

    private static final int HOST_VERSION = 1;

    private final SendQueue sendQueue;

    private final Map<String, Integer> tagMap = new HashMap<>();

    private final Connector connector;

    private int nextTagId = 1;

    public Connection(Connector connector) {
        this.connector = connector;
        sendQueue = new SendQueue();
    }

    public void connect() {
        new Thread("EboLogger Transport") {
            @Override
            public void run() {
                connectInternal();
            }
        }.start();
    }

    private void connectInternal() {
        try {
            DataOutputStream toClientStream = connector.connect();

            if (toClientStream == null) {
                throw new IOException("Failed to connect via " + connector);
            }

            // Send the version number.
            toClientStream.write(HOST_VERSION);

            sendQueue.startQueue(toClientStream);

            sendIntroduction();

            // For now, we'll wait and let the send queue do things.
            // We're keeping this thread around in case we'll support incoming
            // messages from the client at some point.
            // This loop will throw an exception if the connection is severed.

            // TODO: Yeah... this is kind of pointless.
/*            while (true) {
  //              fromClientStream.read();
            }*/
        } catch (IOException e) {
            String msg = e.getMessage();
            Log.e(TAG, "Cannot connect to Telemetry server", e);
            sendQueue.terminate();
        }
    }

    public boolean isTerminated() {
        return sendQueue.isTerminated();
    }

    private void sendIntroduction() {
        DataOutputStream out = sendQueue.startCommand(Commands.WELCOME);
        try {
            out.writeInt(HOST_VERSION);
        } catch (IOException e) {
        }
        sendQueue.endCommand();
    }

    public void sendLogEntry(FunctionalLogMessage logMessage) {
        int tagId = createTagId(logMessage.getTag());

        DataOutputStream out = sendQueue.startCommand(Commands.LOGMSG);
        try {
            out.writeLong(logMessage.getTimestamp());
            out.write(logMessage.getSeverity().getSeverity());
            out.writeInt(logMessage.getMarker() != null ? logMessage.getMarker().getId() : 0);
            out.writeInt(tagId);
            out.writeUTF(logMessage.getFormattedMessage());
            out.writeInt(logMessage.getContext() != null ? logMessage.getContext().getId() : 0);
            out.writeInt(logMessage.getObject() != null ? logMessage.getObject().getId() : 0);
            out.writeInt(logMessage.getThread().getId());
            out.writeInt(logMessage.getCallHierarchy() != null ?
                    logMessage.getCallHierarchy().getId() : 0);
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewThread(TrackedThread thread) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_THREAD);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeInt(thread.getId());
            out.writeUTF(thread.getName());
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewTag(int tagId, String tag) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_TAG);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeInt(tagId);
            out.writeUTF(tag);
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewObject(TrackedObject object) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_OBJECT);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeInt(object.getId());
            out.writeUTF(object.getName());
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewContext(TrackedContext context) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_CONTEXT);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeInt(context.getId());
            out.writeUTF(context.getName());
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewMarker(TrackedMarker marker) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_MARKER);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeInt(marker.getId());
            out.writeUTF(marker.getName());
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewClass(ProgramGraph.TrackedClass clazz) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_CLASS);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeShort((short) clazz.getId());
            out.writeUTF(clazz.getName());
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewMethod(ProgramGraph.TrackedMethod method) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_METHOD);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeShort((short) method.getId());
            out.writeUTF(method.getName());
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewSourceFile(ProgramGraph.TrackedSourceFile sourceFile) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_SOURCE_FILE);
        try {
            out.writeShort((short) sourceFile.getId());
            out.writeUTF(sourceFile.getName());
        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    public void sendNewHierarchy(CallHierarchy hierarchy) {
        DataOutputStream out = sendQueue.startCommand(Commands.NEW_HIERARCHY);
        try {
//            out.writeLong(logEntry.getTimestamp());
            out.writeShort((short) hierarchy.getId());
            out.writeShort((short) hierarchy.getClazz().getId());
            out.writeShort((short) hierarchy.getMethod().getId());
            out.writeShort((short) hierarchy.getSourceFile().getId());
            out.writeShort((short) hierarchy.getLine());
            out.writeShort(hierarchy.getParent() != null ?
                    (short) hierarchy.getParent().getId() : 0);

        } catch (IOException e) {

        }

        sendQueue.endCommand();
    }

    private int createTagId(String tag) {
        Integer result;

        synchronized(tagMap) {
            result = tagMap.get(tag);

            if (result != null) {
                return result;
            }

            // If we don't have a tag ID yet, create a new one.
            result = nextTagId++;
            tagMap.put(tag, result);
        }

        sendNewTag(result, tag);

        return result;
    }
}
