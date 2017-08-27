package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Model;
import com.ebomike.ebologger.client.model.Severity;
import com.ebomike.ebologger.client.transport.Connection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements ClientUiInterface, Initializable {
    @FXML
    private Pane logPane;

    @FXML
    private ComboBox<Severity> minSeverity;

    @FXML
    private TextField substring;

    @FXML
    private StackPane logview;

    @FXML
    private DateAxis dateAxis;

    @FXML
    private StackPane master;

    private Window window;

    private Model model;

    private Timeline canvas;

    private Application application;

    private LogList logList;

    private final TimelineView timelineView;

    public ClientController() {
        timelineView = new TimelineView();
    }

    @Override
    public void addLog(LogMsg msg) {
        Platform.runLater(() -> {
            timelineView.add(msg);
//            Platform.runLater(() -> logs.scrollTo(logsList.size() - 1));
        });
    }
/*
    private void createTestData() {
        HostThread thread = new HostThread(1, "Main Thread");
        HostThread workerThread = new HostThread(2, "Worker Thread");

        HostObject object = new HostObject(1, "Object 1");
        HostObject object2 = new HostObject(2, "Object 2");

        long baseTime = System.currentTimeMillis();
        logsList.add(new LogMsg(baseTime, Severity.INFO.getId(), 0, 1, "tag", "Simple Marker",
                null, thread, null, null));
        logsList.add(new LogMsg(baseTime + 50, Severity.ERROR.getId(), 0, 1, "tag", "Error",
                null, thread, null, null));
        logsList.add(new LogMsg(baseTime + 1000, Severity.WARNING.getId(), 0, 2, "other", "One second warning",
                null, thread, object2, null));
        logsList.add(new LogMsg(baseTime + 1100, Severity.WTF.getId(), 0, 2, "other", "WTF",
                null, thread, null, null));

        logsList.add(new LogMsg(baseTime + 1050, Severity.INFO.getId(), 0, 3, "gat", "Worker Marker",
                null, workerThread, object, null));
    }
*/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        TimelineShapes canvas = new TimelineShapes();
        canvas = new Timeline();
        canvas.setTimelineView(timelineView);
        canvas.setModel(model);

        logview.getChildren().add(canvas);

        dateAxis.setTimelineView(timelineView);
        dateAxis.widthProperty().bind(logview.widthProperty());

        canvas.widthProperty().bind(logview.widthProperty());
        canvas.heightProperty().bind(logview.heightProperty());

        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOnShowing(e -> System.out.println("showing"));

        contextMenu.setOnShown(e -> System.out.println("shown"));

        MenuItem item1 = new MenuItem("About");
        item1.setOnAction(e -> System.out.println("About"));
        MenuItem item2 = new MenuItem("Preferences");
        item2.setOnAction(e -> System.out.println("Preferences"));
        contextMenu.getItems().addAll(item1, item2);

        substring.textProperty().bindBidirectional(timelineView.getFilter().substringProperty());
        minSeverity.setItems(FXCollections.observableArrayList(Severity.values()));
        minSeverity.valueProperty().bindBidirectional(timelineView.getFilter().minSeverityProperty());

        logview.setOnContextMenuRequested(e ->
                contextMenu.show(logview.getScene().getWindow()));

        new ScrollNavigator(timelineView, logview);
    }

    public static ClientUiInterface create(Application application, Connection connection) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    application.getClass().getResource(
                            "ui/client.fxml"
                    )
            );

            Parent root = loader.load();

            ClientController controller = loader.getController();
            controller.application = application;
            controller.logList = LogList.create(application, controller.logPane, controller.timelineView);

            Stage stage = new Stage();
            stage.setTitle("EboLogger " + connection.getHostName());
            stage.setScene(new Scene(root, 1000, 500));
            stage.show();
            controller.window = stage;

//            TimelineTooltip tooltip = TimelineTooltip.create(application, connection);
//            controller.attach(tooltip);

            return controller;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void attach(TimelineTooltip node) {
        master.getChildren().add(node.getRoot());
        master.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            Bounds bounds = canvas.getBoundsInLocal();
            Bounds screenBounds = canvas.localToScreen(bounds);
            Timeline.RenderBucket bucket = canvas.getRenderBucket(
                    event.getScreenX() - screenBounds.getMinX(),
                    event.getScreenY() - screenBounds.getMinY());

            if (bucket == null || bucket.getEventCount() == 0) {
                node.getRoot().setVisible(false);
            } else {
                Bounds popupBounds = master.getBoundsInLocal();
                Bounds screenPopupBounds = master.localToScreen(popupBounds);
                node.setContents(bucket);
                node.move(event.getScreenX() - screenPopupBounds.getMinX(),
                        event.getScreenY() - screenPopupBounds.getMinY()
                        );
                node.getRoot().setVisible(true);
//            Bounds popup = node.getRoot().getBoundsInLocal();
//            Bounds screenBounds = canvas.localToScreen(bounds);
            }
        });
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
        timelineView.setModel(model);
        canvas.setModel(model);
    }

    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save EboLogger File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EboLogger Files", "*.ebl"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            try {
                FileOutputStream output = new FileOutputStream(selectedFile);
                model.save(output);
                output.close();
            } catch (IOException e) {
                // TODO: Display error
            }
        }
    }

}
