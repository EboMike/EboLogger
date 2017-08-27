package com.ebomike.ebologger.client;

import com.ebomike.ebologger.client.transport.Connection;
import com.ebomike.ebologger.client.transport.Protocol;
import com.ebomike.ebologger.client.ui.ClientController;
import com.ebomike.ebologger.client.ui.ClientUiInterface;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Text status;

    @FXML
    private ListView connections;

    private Application application;

    private ObservableList<String> hosts = FXCollections.observableArrayList();

    public void setStatus(String msg) {
        status.setText(msg);
    }

    public void addConnection(String name) {
        hosts.add(name);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert status != null;
        assert connections != null;

        connections.setItems(hosts);
    }

    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open EboLogger File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EboLogger Files", "*.ebl"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                FileInputStream input = new FileInputStream(selectedFile);
                Connection connection = new Connection(input, new Protocol(), selectedFile.getPath());
                ClientUiInterface uiInterface = ClientController.create(application, connection);
                connection.setUiInterface(uiInterface);
                connection.run();
            } catch (IOException e) {
                // TODO: Display error
            }
        }
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
