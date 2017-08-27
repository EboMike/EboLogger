package com.ebomike.ebologger.transport;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A {@link Connector} implementation that sends the stream via TCP to a certain address.
 * Unless constructed with a specific IP, it will try to use discovery to identify the address
 * to send all data to.
 */
public class NetworkConnector implements Connector {
    private static final String TAG = "LogConnector";

    private static final int CLIENT_PORT = 8023;

    private static final int DISCOVERY_PORT = 8024;

    @Nullable
    private final String target;

    private final int port;

    public NetworkConnector() {
        this(null, 0);
    }

    public NetworkConnector(@Nullable String target, int port) {
        this.target = target;
        this.port = port == 0 ? CLIENT_PORT : port;
    }

    @Override
    @WorkerThread
    @Nullable
    public DataOutputStream connect() throws IOException {
        Log.v(TAG, "Connecting to EboLogger...");
        String client = target;

        if (client == null) {
            Log.v(TAG, "No target provided for EboLogger - using discovery");
            client = Discovery.discover(DISCOVERY_PORT);
        }

        if (client == null) {
            return null;
        }

        Socket socket = new Socket(client, port);
        return new DataOutputStream(socket.getOutputStream());
    }
}
