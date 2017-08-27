package com.ebomike.ebologger.transport;

import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Tries to find the address on the network that belongs to the logging app by sending out an
 * IPv4 broadcast.
 *
 * Adapted from Michiel de Mey's post:
 * https://michieldemey.be/blog/network-discovery-using-udp-broadcast/
 */
public class Discovery {
    private static final String TAG = "LoggerDiscovery";

    private static final String HOST_CHALLENGE = "EBOLOGGER_DISCOVERY";

    private static final String CLIENT_RESPONSE = "EBOLOGGER_UI";

    @Nullable
    public static String discover(int port) {
        if (isEmulator()) {
            return "10.0.2.2";
        }

        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = HOST_CHALLENGE.getBytes();

            //FF02:0:0:0:0:0:0:2
            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        InetAddress.getByName("255.255.255.255"), port);
                c.send(sendPacket);
                Log.i(TAG, "Discovery packet sent: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                                broadcast, port);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }

                    Log.i(TAG, "Discovery request packet sent to: " + broadcast.getHostAddress() +
                            "; Interface: " + networkInterface.getDisplayName());
                }
            }

            //Wait for a response
            byte[] recvBuf = new byte[32];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            // Verify response
            String message = new String(receivePacket.getData()).trim();
            if (message.equals(CLIENT_RESPONSE)) {
                return receivePacket.getAddress().getHostAddress();
            }

            c.close();
        } catch (IOException ex) {
            Log.e(TAG, "Error discovering client", ex);
        }

        return null;
    }

    /**
     * Returns true if this is running on an emulator, using a horribly hacky method.
     */
    private static boolean isEmulator() {
        String fingerPrint = Build.FINGERPRINT;

        return fingerPrint.contains("vbox") || fingerPrint.contains("generic");
    }
}
