package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.Model;

public interface FilterSpec {
    LogFilter[] generateFilters(LogFilter baseFilter, Model model);
}
