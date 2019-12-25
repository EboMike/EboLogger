package com.ebomike.ebologger;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration object for the EboLogger environment as a whole. To configure EboLogger,
 * create a {@link GlobalConfig.Builder} object, instantiate it with
 * {@link GlobalConfig.Builder#build}, then call {@link GlobalConfig.Builder#activate} to make it
 * the global configuration. This must be done before EboLogger has been used, i.e. before any
 * log message has been created.
 */
public class GlobalConfig {
    /** Sender ID that is used for the local logger (@link AndroidLogSender). */
    public static final int DEFAULT_LOCAL_LOGGER_ID = 1;

    /** Sender ID that is used for the stream logger (@link LogStreamSender). */
    public static final int DEFAULT_STREAM_SENDER_ID = 2;

    /** The currently active global configuration. */
    private static GlobalConfig activeConfig;

    /** Mutex being used to synchronize all access to the active configuration. */
    private static final Object activeConfigMutex = new Object();

    /** All active log senders. These do not include the default senders. */
    private final List<LogSender> logSenders;

    /** Set to true once this config is used to initialize EboLogger. */
    private boolean used;

    public static class Builder {
        private final List<LogSender> logSenders = new ArrayList<>();

        private boolean disableDefaultSenders = false;

        public Builder disableDefaultSenders() {
            disableDefaultSenders = true;
            return this;
        }

        public Builder addSender(LogSender sender) {
            logSenders.add(sender);
            return this;
        }

        public GlobalConfig build() {
            return new GlobalConfig(logSenders, disableDefaultSenders);
        }
    }

    /**
     * This will activate this configuration, so when the next log message is created, this
     * configuration will be used. This must be called before any log messages are created, or
     * else the call with throw an IllegalStateException.
     */
    public void activate() {
        // Make sure we {@link #get} has not been called yet. Once the config has been read, it
        // can no longer be changed.
        synchronized(activeConfigMutex) {
            if (activeConfig != null && activeConfig.used) {
                throw new IllegalStateException("GlobalConfig cannot be activated after the " +
                    "logger has been initialized already.");
            }

            activeConfig = this;
        }
    }

    /**
     * Called when this configuration is about to be used to set up EboLogger. At this point,
     * any initialization logic can be done such as creating default senders.
     */
    public void init() {
    }

    private GlobalConfig(List<LogSender> logSenders, boolean disableDefaultSenders) {
        List<LogSender> allLogSenders = new ArrayList<>();

        if (!disableDefaultSenders) {
            allLogSenders.addAll(DefaultGlobalConfig.createDefaultSenders());
        }
        allLogSenders.addAll(logSenders);
        this.logSenders = allLogSenders;
    }

    /**
     * Returns all @{link LogSender} objects. This includes default senders (unless disabled) as
     * well as user-provided ones.
     */
    public List<LogSender> getLogSenders() {
        return logSenders;
    }

    /**
     * Obtains the active configuration. This will create a default one if none was set, or the
     * one that was most recently activated by the user via @{link GlobalConfig#activate}.
     */
    public static GlobalConfig get() {
        synchronized(activeConfigMutex) {
            if (activeConfig == null) {
                activeConfig = new GlobalConfig.Builder().build();
            }

            // Mark it as used - from now on, activeConfig may no longer be replaced.
            activeConfig.used = true;
            return activeConfig;
        }
    }

    /** Resets the global configuration to its original state. */
    @VisibleForTesting
    public static void reset() {
        activeConfig = null;
    }
}
