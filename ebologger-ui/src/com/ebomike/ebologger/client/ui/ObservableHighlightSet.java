package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObservableHighlightSet extends SimpleSetProperty<Integer> {
    private final LogFilter filter;

    private List<LogMsg> logList;

    private ObservableSet<Integer> filteredList = FXCollections.observableSet();

    public ObservableHighlightSet(LogFilter filter) {
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
            filteredList.clear();
            return;
        }

        Set<Integer> newFilteredSet = new HashSet<>(logList.size());

        for (LogMsg logMsg : logList) {
            if (filter.passesFilter(logMsg)) {
                newFilteredSet.add(logMsg.getId());
            }
        }

        filteredList.addAll(newFilteredSet);
    }

    public boolean add(LogMsg msg) {
        if (filter.passesFilter(msg)) {
            filteredList.add(msg.getId());
            return true;
        }

        return false;
    }
}
