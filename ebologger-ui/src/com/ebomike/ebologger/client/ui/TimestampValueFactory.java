package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampValueFactory implements Callback<TableColumn.CellDataFeatures<LogMsg, String>, ObservableValue<String>> {
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<LogMsg, String> column) {
        String date = sdf.format(new Date(column.getValue().getTimestamp()));

        return new ReadOnlyObjectWrapper<>(date);
    }
}
