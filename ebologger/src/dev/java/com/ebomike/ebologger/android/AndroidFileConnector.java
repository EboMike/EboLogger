package com.ebomike.ebologger.android;

import android.content.Context;

import com.ebomike.ebologger.transport.FileConnector;

import java.io.File;

/**
 * An Android-specific version of {@link FileConnector} that will write the file to the device
 * in a location private to the app.
 */
public class AndroidFileConnector extends FileConnector {
    public AndroidFileConnector(Context context) {
        super(new File(context.getExternalFilesDir(null), getName()).getPath());
    }
}
