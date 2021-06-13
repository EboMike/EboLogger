package com.ebomike.ebologger.model;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An instance of this class represents a full or partial stack trace. The object itself contains
 * one specific stack trace element (class, method, source file, line number), and optionally points
 * to a parent.
 *
 * This object should be created by calling {@link ProgramGraph#createCallHierarchy}, which will try
 * to create a CallHierarchy object from a stack trace using as many existing partial call
 * hierarchies as possible.
 */
public class CallHierarchy {
    private final ProgramGraph.TrackedClass clazz;

    private final ProgramGraph.TrackedMethod method;

    private final ProgramGraph.TrackedSourceFile sourceFile;

    private final int line;

    private final int id;

    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Nullable
    private final CallHierarchy parent;

    CallHierarchy(ProgramGraph.TrackedClass clazz, ProgramGraph.TrackedMethod method,
                         ProgramGraph.TrackedSourceFile sourceFile, int line,
                        @Nullable CallHierarchy parent) {
        this.clazz = clazz;
        this.method = method;
        this.sourceFile = sourceFile;
        this.line = line;
        this.parent = parent;
        id = nextId.incrementAndGet();
    }

    public ProgramGraph.TrackedClass getClazz() {
        return clazz;
    }

    public ProgramGraph.TrackedMethod getMethod() {
        return method;
    }

    public ProgramGraph.TrackedSourceFile getSourceFile() {
        return sourceFile;
    }

    public int getLine() {
        return line;
    }

    @Nullable
    public CallHierarchy getParent() {
        return parent;
    }

    public int getId() {
        return id;
    }

    /**
     * Unpacks this object to a partial or full stack trace. Converts this object into an array of
     * StackTraceElement objects.
     */
    @VisibleForTesting
    public StackTraceElement[] unpackHierarchy() {
        List<StackTraceElement> result = new ArrayList<>();
        CallHierarchy hierarchy = this;

        do {
            StackTraceElement element = hierarchy.unpack();
            result.add(element);
            hierarchy = hierarchy.parent;
        } while (hierarchy != null);

        return result.toArray(new StackTraceElement[0]);
    }

    /**
     * Converts this object back to a StackTraceElement.
     */
    @VisibleForTesting
    public StackTraceElement unpack() {
        return new StackTraceElement(clazz.getName(), method.getName(), sourceFile.getName(),
                line);
    }
}
