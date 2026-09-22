package com.ebomike.ebologger.android;

import android.app.Application;

public class ActivityTracker {
  private static final ActivityTracker dummy = new ActivityTracker();

  public static ActivityTracker get() {
    return dummy;
  }

  public void register(Application application) {
  }
}
