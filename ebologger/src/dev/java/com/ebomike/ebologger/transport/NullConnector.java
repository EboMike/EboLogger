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

public class NullConnector implements Connector {
    @Nullable
    @Override
    public DataOutputStream connect() throws IOException {
        return null;
    }
}
