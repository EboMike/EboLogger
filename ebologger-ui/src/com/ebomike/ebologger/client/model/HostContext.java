package com.ebomike.ebologger.client.model;

import com.ebomike.ebologger.client.transport.Commands;

import java.io.DataOutputStream;
import java.io.IOException;

public class HostContext implements StreamSerializable {
    private final int id;

    private final String name;

    public HostContext(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.write(Commands.NEW_CONTEXT);
        out.writeInt(id);
        out.writeUTF(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
