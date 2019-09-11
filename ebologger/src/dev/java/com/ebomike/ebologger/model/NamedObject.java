package com.ebomike.ebologger.model;

import androidx.annotation.AnyThread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for a tracking object. This is an object that corresponds to something being tracked -
 * a Thread or an object, for example. A tracked object must have an ID and a name.
 */
public class NamedObject {
    private final String name;

    private final int id;

    private static final AtomicInteger nextId = new AtomicInteger(1);

    /**
     * Constructs this object and automatically assigns it a new ID.
     *
     * @param name Name for this object.
     */
    @AnyThread
    public NamedObject(String name) {
        this.name = name;
        this.id = nextId.incrementAndGet();
    }

    public NamedObject(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
