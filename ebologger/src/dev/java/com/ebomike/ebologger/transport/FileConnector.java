package com.ebomike.ebologger.transport;

import android.app.Application;
import android.support.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A {@link Connector} that will write all data to a file.
 * The current implementation creates a file in the current directory, with the current time
 * and date in the filename.
 */
public class FileConnector implements Connector {
    private final String filename;

    public FileConnector() {
        filename = getName();
    }

    public FileConnector(String filename) {
        this.filename = filename;
    }

    /**
     * Generates a filename using the current time and date.
     */
    protected static String getName() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss", Locale.US);
        return String.format("ebologger-%s.elb", sdf.format(new Date()));
    }

    @Nullable
    @Override
    public DataOutputStream connect() throws IOException {
        FileOutputStream stream = new FileOutputStream(filename);
        return new DataOutputStream(stream);
    }
}
