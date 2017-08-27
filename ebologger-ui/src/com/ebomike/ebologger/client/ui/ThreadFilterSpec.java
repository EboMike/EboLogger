package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.HostThread;
import com.ebomike.ebologger.client.model.Model;

public class ThreadFilterSpec implements FilterSpec {
    public LogFilter[] generateFilters(LogFilter baseFilter, Model model) {
        LogFilter[] filters = new LogFilter[model.getThreads().size()];
        int index = 0;

        for (HostThread thread : model.getThreads()) {
            LogFilter filter = baseFilter.cloneFilter();
            filter.setThreadFilter(thread);
            filters[index++] = filter;
        }

        return filters;
    }
}