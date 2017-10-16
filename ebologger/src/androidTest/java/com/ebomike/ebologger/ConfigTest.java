package com.ebomike.ebologger;

import com.ebomike.ebologger.EboLogger.LogLevel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class ConfigTest {

    @Test
    public void testGlobalLevel() {
        Config config = new Config.Builder()
                .setMinLevel(LogLevel.WARNING)
                .build();

        assertThat(config.shouldLog(1, LogLevel.DEBUG), equalTo(false));
        assertThat(config.shouldLog(1, LogLevel.WARNING), equalTo(true));
        assertThat(config.shouldLog(1, LogLevel.ERROR), equalTo(true));
    }

    @Test
    public void testSenderLevel() {
        Config config = new Config.Builder()
                .setMinLevel(LogLevel.ERROR)
                .setMinSenderLevel(2, LogLevel.INFO)
                .build();

        assertThat(config.shouldLog(1, LogLevel.INFO), equalTo(false));
        assertThat(config.shouldLog(2, LogLevel.DEBUG), equalTo(false));
        assertThat(config.shouldLog(2, LogLevel.INFO), equalTo(true));
        assertThat(config.shouldLog(2, LogLevel.ERROR), equalTo(true));
    }

    @Test
    public void testParentConfig() {
        Config baseConfig = new Config.Builder()
                .setMinLevel(LogLevel.ERROR)
                .setMinSenderLevel(2, LogLevel.INFO)
                .build();

        Config config = new Config.Builder()
                .setMinSenderLevel(3, LogLevel.WARNING)
                .setParent(baseConfig)
                .build();

        assertThat(config.shouldLog(2, LogLevel.DEBUG), equalTo(false));
        assertThat(config.shouldLog(2, LogLevel.INFO), equalTo(true));
        assertThat(config.shouldLog(3, LogLevel.INFO), equalTo(false));
        assertThat(config.shouldLog(3, LogLevel.WARNING), equalTo(true));

        // Fallback to parent
        assertThat(config.shouldLog(1, LogLevel.INFO), equalTo(false));
        assertThat(config.shouldLog(1, LogLevel.ERROR), equalTo(true));
    }

    @Test
    public void testMinLevelOverride() {
        Config baseConfig = new Config.Builder()
                .setMinLevel(LogLevel.ERROR)
                .build();

        Config config = new Config.Builder()
                .setMinLevel(LogLevel.INFO)
                .setParent(baseConfig)
                .build();

        assertThat(config.shouldLog(1, LogLevel.DEBUG), equalTo(false));
        assertThat(config.shouldLog(1, LogLevel.INFO), equalTo(true));
    }
}
