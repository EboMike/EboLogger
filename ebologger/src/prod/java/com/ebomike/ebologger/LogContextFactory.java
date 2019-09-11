package com.ebomike.ebologger;

import androidx.annotation.Nullable;

public class LogContextFactory {
    private static final NullLogContext DUMMY_CONTEXT = new NullLogContext();

    public static final LogContext create(String context) {
        return DUMMY_CONTEXT;
    }

    public static final void pop(@Nullable String context) {
    }
}
