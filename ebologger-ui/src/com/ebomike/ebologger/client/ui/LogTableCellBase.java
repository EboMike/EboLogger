package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.HostCallHierarchy;
import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Model;
import com.ebomike.ebologger.client.model.Severity;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.Map;

class LogTableCellBase extends TableCell<LogMsg, String> {
    private static final Map<Integer, Color> SEVERITY_COLORS = new HashMap<>();

//    private final Model model;

    static {
        SEVERITY_COLORS.put(Severity.VERBOSE.getId(), Color.DARKSLATEGRAY);
        SEVERITY_COLORS.put(Severity.DEBUG.getId(), Color.BLUE);
        SEVERITY_COLORS.put(Severity.INFO.getId(), Color.BLACK);
        SEVERITY_COLORS.put(Severity.WARNING.getId(), Color.ORANGE);
        SEVERITY_COLORS.put(Severity.ERROR.getId(), Color.RED);
        SEVERITY_COLORS.put(Severity.WTF.getId(), Color.RED);
    }

    public LogTableCellBase(TableColumn<LogMsg, String> col) {
        //col.get
    }
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item);
            LogMsg logMsg = (LogMsg) (getTableRow().getItem());
            if (logMsg != null) {
                Color color = SEVERITY_COLORS.get(logMsg.getSeverity());

                if (color != null) {
                    setTextFill(color);
                }

                if (logMsg.getHierarchy() != null) {
                    HostCallHierarchy hierarchy = logMsg.getHierarchy();

                    StringBuilder callstack = new StringBuilder(1024);

                    while (hierarchy != null) {
                        callstack.append(logMsg.getModel().getClassName(hierarchy.getClassId()));
                        callstack.append('.');
                        callstack.append(logMsg.getModel().getMethodName(hierarchy.getMethodId()));
                        callstack.append('(');
                        callstack.append(logMsg.getModel().getSourceFile(hierarchy.getSourceFileId()));
                        callstack.append(':');
                        callstack.append(hierarchy.getLine());
                        callstack.append(")\n");

                        hierarchy = hierarchy.getParent();
                    }

                    setTooltip(new Tooltip(callstack.toString()));
                } else {
                    setTooltip(new Tooltip("No callstack"));
                }
            }
        }
    }
}
