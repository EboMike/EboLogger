package com.ebomike.ebologger.transport;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Establishes a connection to send the log stream to. This could be a file, or a TCP connection
 * to the app.
 */
public interface Connector {
    /**
     * Tries to establish a connection. This call will block until the connection has been
     * established, or until it failed.
     *
     * @return The output stream to send all data to.
     * @throws IOException If an error occurs while creating the connection.
     */
    @WorkerThread
    @Nullable
    DataOutputStream connect() throws IOException;
}
