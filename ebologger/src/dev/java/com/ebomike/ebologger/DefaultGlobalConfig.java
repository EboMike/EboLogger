package com.ebomike.ebologger;

import com.ebomike.ebologger.android.AndroidLogSender;
import com.ebomike.ebologger.model.ProgramGraph;

import java.util.ArrayList;
import java.util.List;

public class DefaultGlobalConfig {
    public static List<LogSender> createDefaultSenders() {
        List<LogSender> result = new ArrayList<>();

        result.add(new AndroidLogSender());
        result.add(new LogStreamSender(ProgramGraph.get()));
        return result;
    }
}
