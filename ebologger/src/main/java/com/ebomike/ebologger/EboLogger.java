package com.ebomike.ebologger;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

import com.ebomike.ebologger.android.AndroidLoggerFactory;
import com.ebomike.ebologger.model.LogMessage;
import com.ebomike.ebologger.model.ReadableLogMessage;

/**
 * A logger, typically used per object, that allows for sending logging messages. These messages
 * can be shown in the native debugging interface (like Android's logcat) and can also be sent
 * across the network to a EboLogger host, which will store and visualize them. They could also
 * be saved in plaintext to a file. What exactly is done with them depends on which
 * {@link LogSender} implementations are active, see {@link GlobalConfig}.
 *
 * <p>The most common way to generate instantiate a logger is by adding
 *
 * <p><code>private final EboLogger logger = EboLogger.get(this);</code>
 *
 * <p>inside a class. It's also possible to have a static instance:
 *
 * <p><code>private static final EboLogger logger = logger.get();</code>
 *
 * <p>It's preferable to have a per-instance EboLogger in order to store this additional
 * information - it can often be useful to see which specific object sent a message.
 *
 * <p>Messages can be sent either with the Android-compatible syntax
 * (<code>logger.w(TAG, "warning"</code>), or preferably by starting with the severity and
 * chaining additional information, for example:
 *
 * <p><code>logger.info().exception(exception).marker(marker).log(message)</code>
 *
 * Any tag other than the severity and the log message are optional. The severity must be first,
 * and log() must be last.
 */
public abstract class EboLogger {
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

    private final Config config;

    protected EboLogger(Config config) {
        this.config = config;
    }

    @AnyThread
    public static EboLogger get() {
        return new EboLogger.Builder().build();
    }

    @AnyThread
    public static EboLogger get(Object object) {
        return new EboLogger.Builder().setObject(object).build();
    }

    @AnyThread
    public static EboLogger get(Object object, String tag) {
        return new EboLogger.Builder().setObject(object).setTag(tag).build();
    }

    @AnyThread
    private static String getCallingClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

        // Seems to be this:
        // 0=dalvik.system.VMStack
        // 1=java.lang.Thread
        // 2=com.ebomike.ebologger.EboLogger
        String className = "Base";

        for (int index = 3; index < stElements.length; index++) {
            className = stElements[index].getClassName();
            if (!className.startsWith("com.ebomike.ebologger.EboLogger")) {
                int delimiter = className.lastIndexOf('.');
                if (delimiter >= 0) {
                    className = className.substring(delimiter + 1);
                }
                break;
            }
        }

        return className;
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
    public void sendMessage(ReadableLogMessage message) {
        GlobalConfig globalConfig = GlobalConfig.get();
        LogLevel severity = message.getSeverity();

        for (LogSender sender : globalConfig.getLogSenders()) {
            if (config.shouldLog(sender.getSenderId(), severity)) {
                sender.sendMessage(message);
            }
        }
    }

    @AnyThread
    protected abstract LogMessage createLogMessage(LogLevel severity);

    /**
     * Returns the logging tag to use by default for this logger. This is often the class name of
     * the object that this logger was created for.
     **/
    protected abstract String getTag();

    public static class Builder {
        @Nullable
        private String tag;

        @Nullable
        private Object object;

        @Nullable
        private LogLevel minSeverity;

        @Nullable
        private Config config;

        public Builder setMinSeverity(LogLevel minSeverity) {
            this.minSeverity = minSeverity;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setObject(Object object) {
            this.object = object;
            return this;
        }

        public EboLogger build() {
            if (config == null) {
                config = Config.getBaseConfig();
            }

            if (minSeverity != null) {
                config = new Config.Builder()
                        .setParent(config)
                        .setMinLevel(minSeverity)
                        .build();
            }

            if (tag == null) {
                String className = getCallingClassName();
                int len = Math.min(className.length(), 15);
                tag = className.substring(0, len);
            }

            return AndroidLoggerFactory.createLogger(tag, object, config);
        }
    }
}
