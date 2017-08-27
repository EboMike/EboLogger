package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Severity;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class SeverityValueFactory implements Callback<TableColumn.CellDataFeatures<LogMsg, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<LogMsg, String> column) {
        return new ReadOnlyStringWrapper(Severity.fromId(column.getValue().getSeverity()).getName());
    }
}
