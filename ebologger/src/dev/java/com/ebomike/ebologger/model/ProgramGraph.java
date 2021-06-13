package com.ebomike.ebologger.model;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

import com.ebomike.ebologger.FunctionalLogger;
import com.ebomike.ebologger.TransportRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * The main graph that keeps track of all logged elements - threads, objects, contexts, markers,
 * and more. Each of those elements has a tracking object, like {@link TrackedThread} for each
 * thread.
 */
public class ProgramGraph {
    // Master switch to disable the graph. Useful if we're not connected to the logger client.
    private boolean disabled;

    private final Map<Long, CallHierarchy> partialStackTraces = new HashMap<>();

    public static class TrackedClass extends NamedObject {
        public TrackedClass(String name) {
            super(name);
        }
    }

    public static class TrackedMethod extends NamedObject {
        public TrackedMethod(String name) {
            super(name);
        }
    }

    public static class TrackedSourceFile extends NamedObject {
        public TrackedSourceFile(String name) {
            super(name);
        }
    }

    private final NamedObjectMap<Object, TrackedObject> objects =
            new NamedObjectMap<Object, TrackedObject>() {
        @Override
        public TrackedObject create(Object key) {
            return createTrackedObject(key);
        }
    };

    private final NamedObjectMap<String, TrackedContext> contexts =
            new NamedObjectMap<String, TrackedContext>() {
        @Override
        public TrackedContext create(String key) {
            return createTrackedContext(key);
        }
    };

    private final NamedObjectMap<Thread, TrackedThread> threads =
            new NamedObjectMap<Thread, TrackedThread>() {
        @Override
        public TrackedThread create(Thread key) {
            return createTrackedThread(key);
        }
    };

    private final NamedObjectMap<String, TrackedMarker> markers =
            new NamedObjectMap<String, TrackedMarker>() {
        @Override
        public TrackedMarker create(String key) {
            return createTrackedMarker(key);
        }
    };

    private final NamedObjectMap<String, TrackedClass> classes =
            new NamedObjectMap<String, TrackedClass>() {
        @Override
        public TrackedClass create(String key) {
            return createTrackedClass(key);
        }
    };

    private final NamedObjectMap<String, TrackedMethod> methods =
            new NamedObjectMap<String, TrackedMethod>() {
        @Override
        public TrackedMethod create(String key) {
            return createTrackedMethod(key);
        }
    };

    private final NamedObjectMap<String, TrackedSourceFile> sourceFiles =
            new NamedObjectMap<String, TrackedSourceFile>() {
        @Override
        public TrackedSourceFile create(String key) {
            return createTrackedSourceFile(key);
        }
    };

    private static ProgramGraph instance = null;

    private static final Object instanceMutex = new Object();

    /**
     *
     * @param thread
     * @return
     */
    public TrackedThread getThread(Thread thread) {
        return threads.get(thread);
    }

    public TrackedThread getCurrentThread() {
        return getThread(Thread.currentThread());
    }

    public TrackedContext getContext(String context) {
        return contexts.get(context);
    }

    @Nullable
    public TrackedObject getObject(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        return objects.get(object);
    }

    @Nullable
    public TrackedMarker getMarker(@Nullable String marker) {
        if (marker == null || marker.isEmpty()) {
            return null;
        }

        return markers.get(marker);
    }

    private TrackedObject createTrackedObject(Object object) {
        String name = object.getClass().getSimpleName() +
                "@" + Integer.toHexString(object.hashCode());

        TrackedObject result = new TrackedObject(name);
        objects.put(object, result);

        TransportRouter.getInstance().getConnection().sendNewObject(result);

        return result;
    }

    private TrackedThread createTrackedThread(Thread thread) {
        TrackedThread result = new TrackedThread(thread.getName());
        threads.put(thread, result);

        TransportRouter.getInstance().getConnection().sendNewThread(result);

        return result;
    }

    private TrackedContext createTrackedContext(String name) {
        TrackedContext result = new TrackedContext(name);
        contexts.put(name, result);

        TransportRouter.getInstance().getConnection().sendNewContext(result);

        return result;
    }

    private TrackedMarker createTrackedMarker(String name) {
        TrackedMarker result = new TrackedMarker(name);
        markers.put(name, result);

        TransportRouter.getInstance().getConnection().sendNewMarker(result);

        return result;
    }

    private TrackedClass createTrackedClass(String name) {
        TrackedClass result = new TrackedClass(name);
        classes.put(name, result);

        TransportRouter.getInstance().getConnection().sendNewClass(result);

        return result;
    }

    private TrackedMethod createTrackedMethod(String name) {
        TrackedMethod result = new TrackedMethod(name);
        methods.put(name, result);

        TransportRouter.getInstance().getConnection().sendNewMethod(result);

        return result;
    }

    private TrackedSourceFile createTrackedSourceFile(String name) {
        TrackedSourceFile result = new TrackedSourceFile(name);
        sourceFiles.put(name, result);

        TransportRouter.getInstance().getConnection().sendNewSourceFile(result);

        return result;
    }

    /**
     * Creates a hash value from a stack trace element. The hash will be created from the class,
     * method, filename, and line number. This method may implicitly create tracked objects for any
     * of them, which will queue up packets to be sent to the stream.
     *
     * @param element StackTraceElement to hash.
     */
    private long createStackTraceElementHash(StackTraceElement element) {
        int classId = classes.get(element.getClassName()).getId();
        int methodId = methods.get(element.getMethodName()).getId();
        String fileName = element.getFileName() == null ? "" : element.getFileName();
        int sourceFileId = sourceFiles.get(fileName).getId();
        int line = element.getLineNumber();

        return createStackTraceElementHash(classId, methodId, sourceFileId, line);
    }

    private static long createStackTraceElementHash(int classId, int methodId, int sourceFileId,
                                                    int line) {
        return (long) sourceFileId << 48 | (long) classId << 32 | (long) methodId << 16 | line;
    }

    private static long startHash() {
        return 1125899906842597L; // prime
    }

    private static long addHash(long currentHash, long newValue) {
        return 31 * currentHash + newValue;
    }

    private CallHierarchy createCallHierarchy(StackTraceElement element, long hash,
                                              @Nullable CallHierarchy
                                             parent) {
        String fileName = element.getFileName() == null ? "" : element.getFileName();
        CallHierarchy hierarchy = new CallHierarchy(classes.get(element.getClassName()),
                methods.get(element.getMethodName()), sourceFiles.get(fileName),
                element.getLineNumber(), parent);

        partialStackTraces.put(hash, hierarchy);

        TransportRouter.getInstance().getConnection().sendNewHierarchy(hierarchy);

        return hierarchy;
    }

    /**
     * Creates a CallHierarchy object from a stack trace. This CallHierarchy object will represent
     * this specific stack trace. It will do that by trying to leverage as many registered
     * stack traces that already exist - ideally this is a stack trace that has already been
     * registered before, so it will return an identical object.
     *
     * Otherwise, it will try to find the part of the stack trace that is already known and start
     * creating new CallHierarchy objects for every frame above that point.
     *
     * The incoming strack trace will automatically be sanitized to remove any code from the
     * logger system.
     *
     * @param trace Stack trace to create a CallHierarchy for.
     * @return CallHierarchy object that will represent that specific stack frame.
     */
    @Nullable
    public CallHierarchy getHierarchy(StackTraceElement[] trace) {
        // Eliminate everything from the logger system
        int start = 2;

        while (start < trace.length &&
                trace[start].getClassName().startsWith("com.ebomike.ebologger.")) {
            start++;
        }

        long hash = startHash();
        int bestHashIndex = trace.length;
        long bestHash = hash;
        CallHierarchy bestParent = null;

        // Start from the very bottom of the stack trace and find out up to which point we have
        // a registered CallHierarchy for.
        for (int x=trace.length-1; x>=start; x--) {
            long elementHash = createStackTraceElementHash(trace[x]);
            hash = addHash(hash, elementHash);

            if (partialStackTraces.get(hash) != null) {
                bestHashIndex = x;
                bestParent = partialStackTraces.get(hash);
                bestHash = hash;
            }
        }

        hash = bestHash;

        // Build up traces from the remaining elements.
        for (int x=bestHashIndex - 1; x>=start; x--) {
            long elementHash = createStackTraceElementHash(trace[x]);
            hash = addHash(hash, elementHash);

            bestParent = createCallHierarchy(trace[x], hash, bestParent);
        }

        return bestParent;
    }

    /**
     * Master switch to disable the logger. This will stop queuing up more data to send,
     * and try to free up resources.
     */
    public void disable() {
        disabled = true;
    }

    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Queue a message to be sent to the output stream, provided that the stream has not been
     * disabled.
     *
     * @param message Message to be sent.
     */
    @AnyThread
    public void log(ReadableLogMessage message) {
        if (TransportRouter.getInstance().getConnection().isTerminated()) {
            disable();
        }

        if (!disabled) {
            TransportRouter.getInstance().getConnection().sendLogEntry(message);
        }
    }

    /**
     * Returns the singleton instance of graph, or creates one if there is none yet.
     */
    @AnyThread
    public static ProgramGraph get() {
        if (instance != null) {
            return instance;
        }

        synchronized(instanceMutex) {
            if (instance == null) {
                instance = new ProgramGraph();
            }

            return instance;
        }
    }
}
