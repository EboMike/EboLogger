package com.ebomike.ebologger.android;

import android.app.Application;
import android.os.Build;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActivityTracker {
    private static ActivityTracker dummy = new ActivityTracker();

    public static ActivityTracker get() {
        return dummy;
    }

    public void register(Application application) {
    }
}
