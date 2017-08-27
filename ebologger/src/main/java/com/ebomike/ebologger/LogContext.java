package com.ebomike.ebologger;

import java.io.Closeable;

public abstract class LogContext implements Closeable {
    @Override
    public abstract void close();
}
