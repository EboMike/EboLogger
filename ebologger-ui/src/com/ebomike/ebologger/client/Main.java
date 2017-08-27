package com.ebomike.ebologger.client;

import com.ebomike.ebologger.client.transport.*;
import com.ebomike.ebologger.client.ui.ClientController;
import com.ebomike.ebologger.client.ui.ClientUiInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application implements ListenerInterface {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "clientlist.fxml"
                )
        );

        Parent root = loader.load();

        controller = loader.getController();
        controller.setApplication(this);

        primaryStage.setTitle("EboLogger");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        new Listener(this, 8023).start();
        new DiscoveryListener(8024).start();
    }

    @Override
    public void setStatus(String status) {
        controller.setStatus(status);
    }

    @Override
    public void addConnection(Connection connection) {
        controller.addConnection(connection.getHostName());
    }

    @Override
    public void removeConnection(Connection connection) {

    }

    @Override
    public ClientUiInterface createClientUi(Connection connection) {
        return ClientController.create(this, connection);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
