package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.transport.Connection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TimelinePopup implements Initializable {
    @FXML
    private ListView<String> loglist;

    private PopupControl popupControl;

    private Window parentWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
/*
            Stage stage = new Stage();
            stage.setTitle("EboLogger " + connection.getHostName());
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
*/
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Element 1");
        loglist.setItems(list);

        loglist.pickOnBoundsProperty().set(false);

//            popupControl.add
    }

    public static TimelinePopup create(Application application, Window parentWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    application.getClass().getResource(
                            "ui/popup.fxml"
                    )
            );

            Parent root = loader.load();
       //     root.pickOnBoundsProperty().set(false);
         //   root.setMouseTransparent(true);
            TimelinePopup controller = loader.getController();
            controller.parentWindow = parentWindow;
            controller.popupControl = new PopupControl();
            controller.init(root);

            return controller;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void init(Parent root) {
        popupControl.getScene().setRoot(root);
        popupControl.setOpacity(0.9);
        root.setMouseTransparent(true);
//        root.setPickOnBounds(false);
//        popupControl.stage.initStyle(StageStyle.TRANSPARENT);

        popupControl.getScene().getRoot().addEventFilter(
                MouseEvent.MOUSE_MOVED, this::passthru);
    }

    private void passthru(MouseEvent event) {
        System.out.println("MM: " + event.getX() + "/" + event.getY());
        MouseEvent clone = event.copyFor(event.getSource(), parentWindow);
        parentWindow.fireEvent(clone);
    }

    public PopupControl getPopupControl() {
        return popupControl;
    }
}
