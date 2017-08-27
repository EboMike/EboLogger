package com.ebomike.ebologger;

import android.support.annotation.Nullable;

import com.ebomike.ebologger.Logger.LogLevel;

public class Config {
    // Minimum level for local logging
    @Nullable
    private LogLevel minLevelLocal;

    // Minimum level for remote logging
    @Nullable
    private LogLevel minLevelRemote;

    private static final Config BASE_CONFIG = new Config();

    // Parent configuration, or null.
    @Nullable
    private Config parent;

    // TODO: Do the whole parent thing.

    public Config() {
        minLevelLocal = FlavorSetup.DEFAULT_MIN_LEVEL_LOCAL;
        minLevelRemote = FlavorSetup.DEFAULT_MIN_LEVEL_REMOTE;
    }

    public Config(Config parent) {
        minLevelLocal = parent.minLevelLocal;
        minLevelRemote = parent.minLevelRemote;
    }

    public Config(LogLevel minLevelLocal, LogLevel minLevelRemote) {
        this.minLevelLocal = minLevelLocal;
        this.minLevelRemote = minLevelRemote;
    }

    /**
     * Returns true if the given message passes the criteria of this config and should be logged
     * to the local device.
     *
     * @param level Severity level of the message
     */
    public boolean shouldLogLocal(LogLevel level) {
        return level.getSeverity() >= minLevelLocal.getSeverity();
    }
    /**
     * Returns true if the given message passes the criteria of this config and should be logged
     * to the remote logger.
     *
     * @param level Severity level of the message
     */
    public boolean shouldLogRemote(LogLevel level) {
        return level.getSeverity() >= minLevelRemote.getSeverity();
    }

    public void setMinLevelLocal(LogLevel minLevel) {
        minLevelLocal = minLevel;
    }

    public void setMinLevelRemote(LogLevel minLevel) {
        minLevelRemote = minLevel;
    }

    public static Config getBaseConfig() {
        return BASE_CONFIG;
    }
}
