package com.ebomike.ebologger.model;

import android.support.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public class TrackedThread extends NamedObject {
    private final Deque<TrackedContext> contexts = new ArrayDeque<>();

    public TrackedThread(String name) {
        super(name);
    }

    public void pushContext(TrackedContext context) {
        contexts.push(context);
    }

    public void popContext(TrackedContext context) {
        if (contexts.isEmpty()) {
            throw new RuntimeException("Too many context pops");
        }

        TrackedContext topContext = contexts.pop();

        if (context != topContext) {
            throw new RuntimeException("Context mismatch - expected " + context +
                    ", found " + topContext);
        }
    }

    public void popContext(@Nullable String context) {
        if (contexts.isEmpty()) {
            throw new RuntimeException("Too many context pops");
        }

        TrackedContext topContext = contexts.pop();

        if (context != null && !topContext.getName().equals(context)) {
            throw new RuntimeException("Context mismatch - expected " + context +
                    ", found " + topContext.getName());
        }
    }

    @Nullable
    public TrackedContext getContext() {
        return contexts.peekLast();
    }
}
