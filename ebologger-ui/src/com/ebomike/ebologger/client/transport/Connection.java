package com.ebomike.ebologger.client.transport;

import com.ebomike.ebologger.client.model.Model;
import com.ebomike.ebologger.client.ui.ClientUiInterface;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Connection extends Thread {
    private static final boolean DEBUG = true;

    private final InputStream inputStream;

    private final String name;

    private final Protocol protocol;

    private ClientUiInterface uiInterface;

    private final Model model = new Model();

    public Connection(InputStream inputStream, Protocol protocol, String name) {
        super("Incoming Data");
        this.inputStream = inputStream;
        this.name = name;
        this.protocol = protocol;
        setDaemon(true);
    }

    public void setUiInterface(ClientUiInterface uiInterface) {
        this.uiInterface = uiInterface;
        uiInterface.setModel(model);
    }

    public String getHostName() {
        return name;
    }

    public void run() {
        CommandContext context = new CommandContext(this, uiInterface, model);
        try {
            DataInputStream inputStream = new DataInputStream(this.inputStream);

            // The very first byte should be the version number.
            int version = inputStream.read();

            while (readCommand(inputStream, context, version)) {}
        } catch (IOException e) {
            System.err.println("Connection to host lost: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean readCommand(DataInputStream input, CommandContext context, int version) throws IOException {
        int command = input.read();
        if (DEBUG) {
            System.out.println("Command: " + command);
        }

        if (command == Commands.CMD_TERMINUS) {
            return false;
        }

        CommandHandler handler = protocol.getCommand(command);
        if (handler == null) {
            throw new IOException("Unknown command " + command);
        }

        handler.execute(context, input, version);
        return true;
    }
}
