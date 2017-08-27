package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.transport.Connection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TimelineTooltip implements Initializable {
    private Parent root;

    @FXML
    private ListView loglist;

    private ObservableList<LogMsg> logs = FXCollections.observableArrayList();

    public Node getRoot() {
        return root;
    }

    public static TimelineTooltip create(Application application, Connection connection) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    application.getClass().getResource(
                            "ui/popup.fxml"
                    )
            );

            Parent root = loader.load();
            TimelineTooltip controller = loader.getController();
            root.setOpacity(0.9);
            controller.root = root;

           // root.addEventFilter(MouseEvent.MOUSE_MOVED, controller::onMouseMove);
            return controller;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setContents(Timeline.RenderBucket renderBucket) {
        logs.clear();

        int maxItems = 15;

        for (LogMsg msg : renderBucket.getLogs()) {
            logs.add(msg);
        }
    }

    public void move(double x, double y) {
//        double w = root.getBoundsInParent().getWidth();
//        double h = root.getBoundsInParent().getHeight();
        double w = loglist.getWidth();
        double h = loglist.getHeight();

        System.out.println("Move: " + x + "/" + y + ", dim=" +
                w + "/" + h);
        System.out.println("layoutY=" +
                root.getLayoutY() + ", boundsMinY=" +
                root.getLayoutBounds().getMinY());

        root.setTranslateX(x - w / 2.0);
        root.setTranslateY(y - h / 2.0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loglist.setItems(logs);
    }
}
