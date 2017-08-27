package com.ebomike.ebologger.client.transport;

import com.ebomike.ebologger.client.ui.ClientUiInterface;

public interface ListenerInterface {
    void setStatus(String status);

    void addConnection(Connection connection);

    void removeConnection(Connection connection);

    ClientUiInterface createClientUi(Connection connection);
}
