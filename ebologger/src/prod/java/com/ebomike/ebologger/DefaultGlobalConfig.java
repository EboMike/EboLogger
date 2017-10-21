package com.ebomike.ebologger;

import com.ebomike.ebologger.android.AndroidLogSender;

import java.util.ArrayList;
import java.util.List;

public class DefaultGlobalConfig {
    public static List<LogSender> createDefaultSenders() {
        List<LogSender> result = new ArrayList<>();

        result.add(new AndroidLogSender(GlobalConfig.DEFAULT_LOCAL_LOGGER_ID));
        return result;
    }
}
