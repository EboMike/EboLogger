package com.ebomike.ebologger;

import com.ebomike.ebologger.EboLogger.LogLevel;

/**
 * This class has prod-specific values and objects.
 */
public class FlavorSetup {
    public static final LogLevel DEFAULT_MIN_LEVEL_LOCAL = LogLevel.WARNING;

    // This is moot, we don't do remote logging in prod.
    public static final LogLevel DEFAULT_MIN_LEVEL_REMOTE = LogLevel.WTF;
}
