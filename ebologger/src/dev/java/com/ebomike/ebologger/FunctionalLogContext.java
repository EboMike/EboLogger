package com.ebomike.ebologger;

import com.ebomike.ebologger.model.ProgramGraph;
import com.ebomike.ebologger.model.TrackedContext;
import com.ebomike.ebologger.model.TrackedThread;

public class FunctionalLogContext extends LogContext {
    private final TrackedContext context;

    private final TrackedThread thread;

    public FunctionalLogContext(String context) {
        ProgramGraph graph = ProgramGraph.get();
        thread = graph.getCurrentThread();
        this.context = graph.getContext(context);
        thread.pushContext(this.context);
    }

    @Override
    public void close() {
        // Verify that we're not jumping threads here.
        ProgramGraph graph = ProgramGraph.get();
        if (thread != graph.getCurrentThread()) {
            throw new IllegalStateException("Context must be closed in same thread in which it was created");
        }

        thread.popContext(context);
    }
}
