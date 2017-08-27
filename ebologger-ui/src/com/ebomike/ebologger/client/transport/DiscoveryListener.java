package com.ebomike.ebologger.client.transport;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * Establishes an listener that waits for incoming broadcasts from EboLogger instances, verifies their payload,
 * and sends a response.
 *
 * Adapted from Michiel de Mey's post:
 * https://michieldemey.be/blog/network-discovery-using-udp-broadcast/
 */
public class DiscoveryListener extends Thread {
    private static final String HOST_CHALLENGE = "EBOLOGGER_DISCOVERY";

    private static final String CLIENT_RESPONSE = "EBOLOGGER_UI";

    private final int port;

    public DiscoveryListener(int port) {
        super("Discovery Listener");
        this.port = port;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            //FF02:0:0:0:0:0:0:2
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                // Waiting for incoming broadcasts.
                byte[] incoming = new byte[32];
                DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
                socket.receive(packet);

                if (new String(incoming).trim().equals(HOST_CHALLENGE)) {
                    // Send response
                    byte[] sendData = CLIENT_RESPONSE.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData,
                            sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                }
            }
        } catch (IOException e) {
            System.out.println("Error listening for incoming discovery packets");
            e.printStackTrace();
        }
    }
}
