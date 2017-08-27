package com.ebomike.ebologger.client.transport;

import java.io.DataInputStream;
import java.io.IOException;

public interface CommandHandler {
    void execute(CommandContext context, DataInputStream input, int version) throws IOException;
}
