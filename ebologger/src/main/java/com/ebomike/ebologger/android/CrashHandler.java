package com.ebomike.ebologger.android;

import com.ebomike.ebologger.Logger;
import com.ebomike.ebologger.Logger.LogLevel;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
//    private Logger logger = Logger.get();

    private Thread.UncaughtExceptionHandler oldHandler;

    private boolean triggered;

    public void install() {
        oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler create() {
        return new CrashHandler();
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        if (!triggered) {
            triggered = true;
            Logger logger = Logger.get("CrashHandler", null, true, LogLevel.DEBUG);

            Throwable throwable = e;

            while (throwable != null) {
                logger.error().exception(e).marker("CRASH").tag("CRASH").log("FATAL ERROR: %s",
                        throwable.getMessage());

                // Even though we're adding the full callstack, it makes sense to add it to the log
                // as well to make it easier to read.
                StackTraceElement[] stackTrace = throwable.getStackTrace();

                for (StackTraceElement element : stackTrace) {
                    logger.error().log("%s.%s (%s:%d)",
                            element.getClassName(),
                            element.getMethodName(),
                            element.getFileName(),
                            element.getLineNumber());
                }

                throwable = throwable.getCause();

                if (throwable != null) {
                    logger.error().log("CAUSED BY:");
                }
            }
        }

        if (oldHandler != null) {
            oldHandler.uncaughtException(t, e);
        } else {
            t.getThreadGroup().uncaughtException(t, e);
        }
    }
}
