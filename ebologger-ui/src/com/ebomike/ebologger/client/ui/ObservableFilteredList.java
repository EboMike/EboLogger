package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ObservableFilteredList extends SimpleListProperty<LogMsg> {
    private final LogFilter filter;

    private List<LogMsg> logList;

    private ObservableList<LogMsg> filteredList = FXCollections.observableArrayList();

    public ObservableFilteredList(LogFilter filter) {
        this.filter = filter;
        set(filteredList);

        filter.substringProperty().addListener(observable -> createFilteredList());
        filter.minSeverityProperty().addListener(observable -> createFilteredList());
        filter.getObservable().addListener(observable -> createFilteredList());
    }

    public void setLogList(List<LogMsg> logList) {
        this.logList = logList;
        createFilteredList();
    }

    private void createFilteredList() {
        if (filter.isPassthrough()) {
            filteredList.setAll(logList);
            return;
        }

        ArrayList<LogMsg> newFilteredList = new ArrayList<>(logList.size());

        for (LogMsg logMsg : logList) {
            if (filter.passesFilter(logMsg)) {
                newFilteredList.add(logMsg);
            }
        }

        filteredList.setAll(newFilteredList);
    }

    @Override
    public boolean add(LogMsg msg) {
        if (filter.passesFilter(msg)) {
            filteredList.add(msg);
            return true;
        }

        return false;
    }
}
