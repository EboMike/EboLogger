package com.ebomike.ebologger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoggerTest {
  @Test
  public void testGet() {
    EboLogger logger = EboLogger.get();

    assertThat(logger.getTag(), equalTo("LoggerTest"));
  }
}
