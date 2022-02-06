package com.ebomike.ebologger.android;

import androidx.annotation.Nullable;

import com.ebomike.ebologger.Config;
import com.ebomike.ebologger.FunctionalLogger;
import com.ebomike.ebologger.model.FunctionalLogMessage;
import com.ebomike.ebologger.model.LogMessage;

class AndroidLogger extends FunctionalLogger {
  AndroidLogger(String tag, @Nullable Object object, Config config) {
    super(tag, object, config);
  }

  @Override
  protected LogMessage createLogMessage(LogLevel severity) {
    return new FunctionalLogMessage(this, getGraph(), severity, System.currentTimeMillis())
        .object(getObject()).tag(getTag());
  }
}
