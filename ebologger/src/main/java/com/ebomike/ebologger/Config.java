package com.ebomike.ebologger;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.EboLogger.LogLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures the behavior of a logger, most notably the minimum severity level before a message
 * is processed (which can be different for each {@link LogSender}. Configurations are hierarchical,
 * so a configuration can choose to override the setting of its parent configuration.
 *
 * <p>Each {@link EboLogger} can have its own configuration, but most will likely simply reference
 * the base configuration object, which is at {@link #getBaseConfig}.
 *
 * <p>Configurations are mutable and may be changed at any time. This can help debugging a
 * particular problem while still minimizing the amount of overall noise.
 */
public class Config {
    /**
     * Overall minimum level. No message less than this will be processed, unless overridden by
     * {@link #minSenderLevel} for a specific sender. In case of null, the parent's configuration
     * (or minSenderLevel) will be used.
     */
    @Nullable
    private LogLevel minLevel;

    /**
     * Minimum level for a given sender, identified by its ID. Supersedes the overall
     * {@link #minLevel}.
     */
    private final Map<Integer, LogLevel> minSenderLevel = new HashMap<>();

    // Parent configuration, or null.
    @Nullable
    private Config parent;

    /**
     * This is the base configuration. Every configuration should eventually have this as its
     * root, i.e. the final parent should be this.
     */
    private static final Config BASE_CONFIG = new Config.Builder()
            .setMinLevel(FlavorSetup.DEFAULT_MIN_LEVEL_LOCAL)
            .build();

    /**
     * Returns true if the given message passes the criteria of this config and should be logged
     * to the given {@link LogSender}. The sender is identified by its ID.
     *
     * @param senderId The ID of the @{link LogSender}.
     * @param level Severity level of the message.
     */
    public boolean shouldLog(int senderId, LogLevel level) {
        LogLevel minForLevel = minSenderLevel.get(senderId);

        if (minForLevel != null) {
            return level.getSeverity() >= minForLevel.getSeverity();
        }

        if (minLevel != null) {
            return level.getSeverity() >= minLevel.getSeverity();
        }

        if (parent != null) {
            return parent.shouldLog(senderId, level);
        }

        // If we get here, it means that the base configuration doesn't have a valid minLevel.
        throw new IllegalStateException("Base configuration should have a minLevel set");
    }

    /**
     * Sets the overall minimum level for this configuration. Messages of lesser severity will
     * not be processed at all unless there is an override for a specific sender with
     * {@link #setMinSenderLevel}.
     *
     * @param minLevel Minimum level for a message to be processed. Null to fall back to the parent.
     */
    public void setMinLevel(@Nullable LogLevel minLevel) {
        this.minLevel = minLevel;

        if (minLevel == null && parent == null) {
            throw new IllegalArgumentException("Base config may not set the minimum level to null");
        }
    }

    public void setMinSenderLevel(int senderId, LogLevel minLevel) {
        minSenderLevel.put(senderId, minLevel);
    }

    private void setParent(Config parent) {
        this.parent = parent;
    }

    public static Config getBaseConfig() {
        return BASE_CONFIG;
    }

    /**
     * Builder object for a Config. Note that unlike many other classes in EboLogger, Config is
     * mutable. The builder is mostly provided as a convenience to make creating a Config easier.
     */
    public static class Builder {
        private Config config = new Config();

        public Builder setMinLevel(LogLevel minLevel) {
            config.setMinLevel(minLevel);
            return this;
        }

        public Builder setMinSenderLevel(int senderId, LogLevel minLevel) {
            config.setMinSenderLevel(senderId, minLevel);
            return this;
        }

        public Builder setParent(Config parent) {
            config.setParent(parent);
            return this;
        }

        public Config build() {
            return config;
        }
    }
}
