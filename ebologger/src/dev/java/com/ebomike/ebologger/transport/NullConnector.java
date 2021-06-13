package com.ebomike.ebologger.transport;

import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

public class NullConnector implements Connector {
    @Nullable
    @Override
    public DataOutputStream connect() throws IOException {
        return null;
    }
}
