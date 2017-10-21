package com.ebomike.ebologger;

import com.ebomike.ebologger.EboLogger.LogLevel;
import com.ebomike.ebologger.android.AndroidLogConnector;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ProdLoggerTest {
    @Mock
    private AndroidLogConnector mockLogConnector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AndroidLogConnector.setLogConnector(mockLogConnector);
    }

    @Test
    public void testSkipInfo() {
        EboLogger logger = EboLogger.get(this);

        logger.verbose().log("Test V");
        logger.debug().log("Test D");
        logger.info().log("Test I");

        verify(mockLogConnector).androidLog(LogLevel.INFO, "ProdLoggerTest", "Test I", null);
        verifyNoMoreInteractions(mockLogConnector);
    }

    @Test
    public void testWithExceptions() {
        EboLogger logger = EboLogger.get(this);
        Throwable throwable = new IllegalStateException("Test");

        logger.verbose().exception(throwable).log("Test V");
        logger.debug().exception(throwable).log("Test D");
        logger.info().exception(throwable).log("Test I");

        verify(mockLogConnector).androidLog(LogLevel.INFO, "ProdLoggerTest", "Test I", throwable);
        verifyNoMoreInteractions(mockLogConnector);
    }
}
