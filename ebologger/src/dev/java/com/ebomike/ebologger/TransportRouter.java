package com.ebomike.ebologger;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ebomike.ebologger.android.AndroidFileConnector;
import com.ebomike.ebologger.transport.Connection;
import com.ebomike.ebologger.transport.Connector;
import com.ebomike.ebologger.transport.FileConnector;
import com.ebomike.ebologger.transport.NetworkConnector;

public class TransportRouter {
    private final Connection connection;

    @Nullable
    private static TransportRouter instance = null;

    private static final Object mutex = new Object();

    private static Connector connector = new NetworkConnector();

    public TransportRouter() {
        connection = new Connection(connector);
        connector = null;
        connection.connect();
    }

    public static void setConnector(@NonNull Connector connector) {
        if (TransportRouter.connector == null) {
            throw new RuntimeException("Router had already been set up");
        }

        TransportRouter.connector = connector;
    }

    @Nullable
    public static Connector getConnector() {
        return connector;
    }

    public static TransportRouter getInstance() {
        synchronized (mutex) {
            if (instance == null) {
                instance = new TransportRouter();
            }

            return instance;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
