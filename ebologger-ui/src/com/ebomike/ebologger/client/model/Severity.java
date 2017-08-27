package com.ebomike.ebologger.client.model;

import java.util.HashMap;
import java.util.Map;

public enum Severity {
    VERBOSE(1, "Verbose"),
    DEBUG(2, "Debug"),
    INFO(3, "Info"),
    WARNING(4, "Warning"),
    ERROR(5, "Error"),
    WTF(6, "WTF");

    private final int id;

    private final String name;

    private static final Map<Integer, Severity> LOOKUP = new HashMap<>();

    static {
        for (Severity severity : Severity.values()) {
            LOOKUP.put(severity.getId(), severity);
        }
    }

    Severity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Severity fromId(int id) {
        Severity severity = LOOKUP.get(id);

        if (severity == null) {
            throw new IllegalArgumentException("Invalid severity ID " + id);
        }

        return severity;
    }
}
