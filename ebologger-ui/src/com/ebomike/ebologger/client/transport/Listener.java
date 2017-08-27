package com.ebomike.ebologger.client.transport;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class Listener extends Thread {
    private final int port;
//    private static final int PORT = 975;

    @Nullable
    private ServerSocket socket;

    private final ListenerInterface listenerInterface;

    public Listener(ListenerInterface listenerInterface, int port) {
        super("Connection Listener");

        this.port = port;
        this.listenerInterface = listenerInterface;
        setDaemon(true);
    }

    public void run() {
        listenerInterface.setStatus("Setting up...");

        try {
            //Log.v("Setting up socket at port %d", PORT);
            System.out.println("Listening");
            socket = new ServerSocket(port);
            //Log.v("Now listening...");

            System.out.println(InetAddress.getLocalHost().toString());
            String bestIp = "";

            // Try to get our own IP.
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (!ni.isLoopback()) {
                    Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();

                    while (inetAddresses.hasMoreElements()) {
                        String in = inetAddresses.nextElement().toString();
                        if (in.indexOf('.') != -1) {
                            if (in.charAt(0) != '0') {
                                bestIp = in;
                            }
                        }
                    }
                }
            }

            listenerInterface.setStatus(String.format("Listening at %s:%d",
                    bestIp, port));

            while (true) {
                Socket connectionSocket = socket.accept();
                System.out.println("Incoming connection from " + connectionSocket.getInetAddress().toString());
                Platform.runLater(() -> {
                    System.out.println("Now creating connection");
                    SocketAddress address = connectionSocket.getRemoteSocketAddress();
                    String name = address != null ? address.toString() : "";

                    try {
                        Connection connection = new Connection(connectionSocket.getInputStream(), new Protocol(), name);
                        listenerInterface.addConnection(connection);
                        connection.setUiInterface(listenerInterface.createClientUi(connection));
                        connection.start();
                    } catch (IOException ex) {
                        // TODO: Display error
                    }
                });
            }
        } catch (IOException e) {
            listenerInterface.setStatus("Error listening on port " + port + ": " + e.getMessage());
            //Log.e(e, "Error listening: %s", e.getMessage());
            e.printStackTrace();
        }
    }
}
