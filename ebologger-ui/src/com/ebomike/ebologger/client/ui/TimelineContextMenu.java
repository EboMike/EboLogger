package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;

public class TimelineContextMenu {
    private final ContextMenu contextMenu;

    private final ContextMenuEvent event;

    private final Node anchor;

    public TimelineContextMenu(ContextMenuEvent event, Node anchor, LogMsg logMsg) {
        MenuItem headerItem = new MenuItem(logMsg.getMsg())    ;
        headerItem.setDisable(true);
        contextMenu = new ContextMenu(headerItem);

        if (logMsg.getObject() != null) {
            MenuItem onlyObject = new MenuItem("Show only object " + logMsg.getObject().getName());
            contextMenu.getItems().add(onlyObject);
        }

        this.anchor = anchor;
        this.event = event;
    }

    public void show() {
        contextMenu.show(anchor, Side.BOTTOM, 0.0, 0.0);
        event.consume();
    }
}
