package com.ebomike.ebologger.client.transport;

import com.ebomike.ebologger.client.model.Model;
import com.ebomike.ebologger.client.ui.ClientUiInterface;

public class CommandContext {
    private final Connection connection;

    private final ClientUiInterface uiInterface;

    private final Model model;

    public CommandContext(Connection connection, ClientUiInterface uiInterface, Model model) {
        this.connection = connection;
        this.uiInterface = uiInterface;
        this.model = model;
    }

    public Connection getConnection() {
        return connection;
    }

    public ClientUiInterface getUiInterface() {
        return uiInterface;
    }

    public Model getModel() {
        return model;
    }
}
