package com.ebomike.ebologger;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ebomike.ebologger.android.AndroidLoggerFactory;
import com.ebomike.ebologger.android.AndroidReleaseLogger;
import com.ebomike.ebologger.model.LogMessage;

/**
 * A logger, typically used per object, that allows for sending logging messages. These messages
 * can be shown in the native debugging interface (like Android's logcat) and can also be sent
 * across the network to a EboLogger host, which will store and visualize them.
 *
 * The most common way to generate instantiate a logger is by adding
 *
 * <code>private final Logger logger = Logger.get(this);</code>
 *
 * inside a class. It's also possible to have a static instance:
 *
 * <code>private static final Logger logger = logger.get();</code>
 *
 * It's preferable to have a per-instance Logger in order to store this additional information -
 * it can often be useful to see which specific object sent a message.
 *
 * Messages can be sent either with the Android-compatible syntax
 * (<code>logger.w(TAG, "warning"</code>), or preferably by starting with the severity and
 * chaining additional information, for example:
 *
 * <code>logger.info().exception(exception).marker(marker).log(message)</code>
 *
 * Any tag other than the severity and the log message are optional. The severity must be first,
 * and log() must be last.
 */
public abstract class Logger {
    public enum LogLevel {
        VERBOSE(1),
        DEBUG(2),
        INFO(3),
        WARNING(4),
        ERROR(5),
        WTF(6);

        private final int severity;

        LogLevel(int severity) {
            this.severity = severity;
        }

        public int getSeverity() {
            return severity;
        }
    };

    @AnyThread
    public static Logger get(Class clazz, boolean enabled) {
        return get(clazz.getSimpleName(), null, enabled, FlavorSetup.DEFAULT_MIN_LEVEL_LOCAL);
    }

    @AnyThread
    public static Logger get(Object object, boolean enabled) {
        return get(object.getClass().getSimpleName(), object, enabled,
                FlavorSetup.DEFAULT_MIN_LEVEL_LOCAL);
    }

    @AnyThread
    public static Logger get(Object object) {
        return get(object, true);
    }

    @AnyThread
    public static Logger get() {
        return get(FlavorSetup.DEFAULT_MIN_LEVEL_LOCAL);
    }

    @AnyThread
    public static Logger get(LogLevel minSeverity) {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

        // Seems to be this:
        // 0=dalvik.system.VMStack
        // 1=java.lang.Thread
        // 2=com.ebomike.ebologger.Logger
/*
        Log.v("EboLOG", "Count=" + stElements.length);
        Log.v("EboLOG", "0=" + stElements[0].getClassName());
        Log.v("EboLOG", "1=" + stElements[1].getClassName());
        Log.v("EboLOG", "2=" + stElements[2].getClassName());
        Log.v("EboLOG", "3=" + stElements[3].getClassName());
        Log.v("EboLOG", "-2=" + stElements[stElements.length-2].getClassName());
        Log.v("EboLOG", "-1=" + stElements[stElements.length-1].getClassName());
        */
        String className = "Base";

        for (int index = 3; index < stElements.length; index++) {
            className = stElements[index].getClassName();
            if (!className.equals("com.ebomike.ebologger.Logger")) {
                int delimiter = className.lastIndexOf('.');
                if (delimiter >= 0) {
                    className = className.substring(delimiter + 1);
                }
                break;
            }
        }

        return get(className, null, true, minSeverity);
    }

    @AnyThread
    public static Logger get(String name, @Nullable Object object, boolean enabled,
                             LogLevel minSeverity) {
        int len = Math.min(name.length(), 15);
        String tag = name.substring(0, len);

        return AndroidLoggerFactory.createLogger(tag, object, minSeverity);
    }

    @AnyThread
    public final void d(String tag, String message) {
        debug().tag(tag).log("%s", message);
    }

    @AnyThread
    public final void d(String tag, String message, Throwable e) {
        debug().exception(e).tag(tag).log("%s", message);
    }

    @AnyThread
    public final void v(String tag, String message) {
        verbose().tag(tag).log("%s", message);
    }

    @AnyThread
    public final void v(String tag, String message, Throwable e) {
        verbose().exception(e).tag(tag).log("%s", message);
    }

    @AnyThread
    public final void i(String tag, String message) {
        info().tag(tag).log("%s", message);
    }

    @AnyThread
    public final void i(String tag, String message, Throwable e) {
        info().exception(e).tag(tag).log("%s", message);
    }

    @AnyThread
    public final void w(String tag, String message) {
        warning().tag(tag).log("%s", message);
    }

    @AnyThread
    public final void w(String tag, String message, Throwable e) {
        warning().exception(e).tag(tag).log("%s", message);
    }

    @AnyThread
    public final void e(String tag, String message) {
        error().tag(tag).log("%s", message);
    }

    @AnyThread
    public final void e(String tag, String message, Throwable e) {
        error().exception(e).tag(tag).log("%s", message);
    }

    @AnyThread
    public final void wtf(String tag, String message) {
        wtf().tag(tag).log("%s", message);
    }

    @AnyThread
    public final void wtf(String tag, String message, Throwable e) {
        wtf().exception(e).tag(tag).log("%s", message);
    }

    @AnyThread
    public final void wtf(String tag, Throwable e) {
        wtf().exception(e).tag(tag).log("%s", e.getMessage());
    }

    @AnyThread
    public final LogMessage debug() {
        return createLogMessage(LogLevel.DEBUG);
    }

    @AnyThread
    public final LogMessage verbose() {
        return createLogMessage(LogLevel.VERBOSE);
    }

    @AnyThread
    public final LogMessage info() {
        return createLogMessage(LogLevel.INFO);
    }

    @AnyThread
    public final LogMessage warning() {
        return createLogMessage(LogLevel.WARNING);
    }

    @AnyThread
    public final LogMessage error() {
        return createLogMessage(LogLevel.ERROR);
    }

    @AnyThread
    public final LogMessage wtf() {
        return createLogMessage(LogLevel.WTF);
    }

    @AnyThread
    protected abstract LogMessage createLogMessage(LogLevel severity);

    /**
     * Returns the logging tag to use by default for this logger. This is often the class name of
     * the object that this logger was created for.
     **/
    protected abstract String getTag();
}
