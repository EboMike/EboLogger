package com.ebomike.ebologger.client.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface StreamSerializable {
    //void load(DataInputStream in, int version);

    void save(DataOutputStream out) throws IOException;
}
