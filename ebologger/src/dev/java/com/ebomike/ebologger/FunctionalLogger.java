package com.ebomike.ebologger;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.model.CallHierarchy;
import com.ebomike.ebologger.model.ProgramGraph;

public abstract class FunctionalLogger extends EboLogger {
    protected final String tag;

    @Nullable
    private final Object object;

    private final String prefix;

    private final boolean createGraph = true;

    private final LogLevel minSeverity;

    private final ProgramGraph graph = ProgramGraph.get();

    public FunctionalLogger(String tag, @Nullable Object object, LogLevel minSeverity) {
        this.tag = tag;
        this.object = object;
        this.minSeverity = minSeverity;

        if(object == null) {
            prefix = "";
        } else {
            prefix = object.getClass().getSimpleName() +
                    "@" + Integer.toHexString(object.hashCode()) + ": ";
        }
    }

    @Override
    public String getTag() {
        return tag;
    }

    public static String getMsg(LogLevel level, String fmt, Object... args) {
        return String.format(fmt, args);
    }

    private CallHierarchy getHierarchy(StackTraceElement[] stackTrace) {
        return graph.getHierarchy(stackTrace);
    }

    protected ProgramGraph getGraph() {
        return graph;
    }

    @Nullable
    protected Object getObject() {
        return object;
    }
}
