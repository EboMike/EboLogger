package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Model;
import com.ebomike.ebologger.client.model.Severity;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class LogList implements EventHandler<ContextMenuEvent>, Initializable {
    @FXML
    private TableView<LogMsg> logs;

    @FXML
    private TableColumn<LogMsg, String> timestamp;

    @FXML
    private TableColumn<LogMsg, String> thread;

    @FXML
    private TableColumn<LogMsg, String> severity;

    @FXML
    private TableColumn<LogMsg, String> context;

    @FXML
    private TableColumn<LogMsg, String> object;

    @FXML
    private TableColumn<LogMsg, String> tag;

    @FXML
    private TableColumn<LogMsg, String> msg;

    private TimelineView timelineView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timestamp.setCellValueFactory(new TimestampValueFactory());
        severity.setCellValueFactory(new SeverityValueFactory());
        context.setCellValueFactory(
                new PropertyValueFactory<>("context")
        );
        thread.setCellValueFactory(
                new PropertyValueFactory<>("thread")
        );
        tag.setCellValueFactory(
                new PropertyValueFactory<>("tag")
        );
        object.setCellValueFactory(
                new PropertyValueFactory<>("object")
        );
        msg.setCellValueFactory(
                new PropertyValueFactory<>("msg")
        );
//        msg.setCellFactory(LogTableCellBase::new);
        msg.setCellFactory(LogTableCellBase::new);

        logs.setOnContextMenuRequested(this);

        logs.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> timelineView.setPrimarySelected(newValue));
    }

    @Override
    public void handle(ContextMenuEvent event) {
        // Find out which cell we hit. This is a fucking stupid way to get it, there must be something better.
        Node node = event.getPickResult().getIntersectedNode();
        ContextMenu contextMenu = new ContextMenu();

        while (node != null) {
            if (node instanceof TableCell) {
                TableCell cell = (TableCell) node;
                LogMsg logMsg = (LogMsg) cell.getTableRow().getItem();
                TableColumn column = cell.getTableColumn();

                if (column == object) {
                    createObjectContext(contextMenu, logMsg);
                } else if (column == thread) {
                    createThreadContext(contextMenu, logMsg);
                } else if (column == tag) {
                    createTagContext(contextMenu, logMsg);
                }

                break;
            }
            node = node.getParent();
        }

        // Display the context menu if there's actually something to display.
        if (contextMenu.getItems().size() > 0) {
            logs.setContextMenu(contextMenu);
        } else {
            logs.setContextMenu(null);
        }
    }

    private void createObjectContext(ContextMenu contextMenu, LogMsg logMsg) {
        if (logMsg.getObject() != null) {
            MenuItem item = new MenuItem("Show only " + logMsg.getObject().getName());
            item.setOnAction((e) -> timelineView.getFilter().setObjectFilter(logMsg.getObject()));
            contextMenu.getItems().add(item);

            MenuItem highlightItem = new MenuItem("Highlight " + logMsg.getObject().getName());
            highlightItem.setOnAction((e) -> timelineView.getHighlighter().setObjectFilter(logMsg.getObject()));
            contextMenu.getItems().add(highlightItem);
        }

        if (timelineView.getFilter().hasObjectFilter()) {
            MenuItem clearItem = new MenuItem("Show all objects");
            clearItem.setOnAction((e) -> timelineView.getFilter().clearObjectFilter());
            contextMenu.getItems().add(clearItem);
        }
    }

    private void createThreadContext(ContextMenu contextMenu, LogMsg logMsg) {
        if (logMsg.getThread() != null) {
            MenuItem item = new MenuItem("Show only " + logMsg.getThread().getName());
            item.setOnAction((e) -> timelineView.getFilter().setThreadFilter(logMsg.getThread()));
            contextMenu.getItems().add(item);

            MenuItem highlightItem = new MenuItem("Highlight " + logMsg.getThread().getName());
            highlightItem.setOnAction((e) -> timelineView.getHighlighter().setThreadFilter(logMsg.getThread()));
            contextMenu.getItems().add(highlightItem);
        }

        if (timelineView.getFilter().hasThreadFilter()) {
            MenuItem clearItem = new MenuItem("Show all threads");
            clearItem.setOnAction((e) -> timelineView.getFilter().clearThreadFilter());
            contextMenu.getItems().add(clearItem);
        }
    }

    private void createTagContext(ContextMenu contextMenu, LogMsg logMsg) {
        if (logMsg.getTag() != null) {
            MenuItem item = new MenuItem("Show only " + logMsg.getTag());
            item.setOnAction((e) -> timelineView.getFilter().setTagFilter(logMsg.getTag()));
            contextMenu.getItems().add(item);

            MenuItem highlightItem = new MenuItem("Highlight " + logMsg.getTag());
            highlightItem.setOnAction((e) -> timelineView.getHighlighter().setTagFilter(logMsg.getTag()));
            contextMenu.getItems().add(highlightItem);
        }

        if (timelineView.getFilter().hasTagFilter()) {
            MenuItem clearItem = new MenuItem("Show all tags");
            clearItem.setOnAction((e) -> timelineView.getFilter().clearTagFilter());
            contextMenu.getItems().add(clearItem);
        }
    }

    private void logMatchView() {
        if (logs.getSelectionModel().getSelectedItem() != timelineView.getPrimarySelected()) {
            logs.getSelectionModel().select(timelineView.getPrimarySelected());
            logs.scrollTo(timelineView.getPrimarySelected());
        }
    }

    private void init(Pane parent, TimelineView timelineView) {
        this.timelineView = timelineView;
        logs.prefHeightProperty().bind(parent.heightProperty());
        logs.prefWidthProperty().bind(parent.widthProperty());
        timelineView.getObservable().addListener(e -> logMatchView());

        ObservableList<LogMsg> logsList = timelineView.getLogsList();
        logs.setItems(logsList);
        logsList.addListener((ListChangeListener<LogMsg>) c -> {
            // This method only inspects the first change, but since we're only
            // really looking for simple changes (i.e. something appended to the end),
            // this is fine.
            c.next();
            if (c.getAddedSize() > 0) {
                logs.scrollTo(logsList.size() - 1);
            }
        });
    }

    public static LogList create(Application application, Pane parent, TimelineView timelineView) {
        assert application != null;
        assert parent != null;
        assert timelineView != null;

        try {
            FXMLLoader loader = new FXMLLoader(
                    application.getClass().getResource(
                            "ui/loglist.fxml"
                    )
            );

            Parent root = loader.load();

            LogList controller = loader.getController();

            parent.getChildren().add(root);
            controller.init(parent, timelineView);
            return controller;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
