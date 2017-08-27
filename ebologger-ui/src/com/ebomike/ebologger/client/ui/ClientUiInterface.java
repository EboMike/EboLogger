package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Model;

public interface ClientUiInterface {
    void addLog(LogMsg msg);

    void setModel(Model model);
}
