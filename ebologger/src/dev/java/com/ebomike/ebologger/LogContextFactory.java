package com.ebomike.ebologger;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.model.ProgramGraph;
import com.ebomike.ebologger.model.TrackedThread;

public class LogContextFactory {
    //@Override
    public static LogContext create(String context) {
        return new FunctionalLogContext(context);
    }

    //@Override
    public static void pop(@Nullable String context) {
        // Verify that we're not jumping threads here.
        ProgramGraph graph = ProgramGraph.get();
        TrackedThread thread = graph.getCurrentThread();

        thread.popContext(context);
    }
}
