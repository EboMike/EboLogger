package com.ebomike.ebologger;

import com.ebomike.ebologger.EboLogger.LogLevel;
import com.ebomike.ebologger.android.AndroidLogConnector;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class DevLoggerTest {
    // The expected tag, should match the class name
    private static final String TAG = "DevLoggerTest";

    @Mock
    private AndroidLogConnector mockLogConnector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AndroidLogConnector.setLogConnector(mockLogConnector);
    }

    @Test
    public void testLogAll() {
        EboLogger logger = EboLogger.get();

        logger.verbose().log("Test V");
        logger.debug().log("Test D");
        logger.info().log("Test I");
        logger.warning().log("Test W");
        logger.error().log("Test E");
        logger.wtf().log("Test WTF");

        verify(mockLogConnector).androidLog(LogLevel.VERBOSE, TAG, "Test V", null);
        verify(mockLogConnector).androidLog(LogLevel.DEBUG, TAG, "Test D", null);
        verify(mockLogConnector).androidLog(LogLevel.INFO, TAG, "Test I", null);
        verify(mockLogConnector).androidLog(LogLevel.WARNING, TAG, "Test W", null);
        verify(mockLogConnector).androidLog(LogLevel.ERROR, TAG, "Test E", null);
        verify(mockLogConnector).androidLog(LogLevel.WTF, TAG, "Test WTF", null);
    }
    @Test

    public void testLogAllWithExceptions() {
        EboLogger logger = EboLogger.get();
        Throwable throwable = new IllegalStateException("Test");

        logger.verbose().exception(throwable).log("Test V");
        logger.debug().exception(throwable).log("Test D");
        logger.info().exception(throwable).log("Test I");
        logger.warning().exception(throwable).log("Test W");
        logger.error().exception(throwable).log("Test E");
        logger.wtf().exception(throwable).log("Test WTF");

        verify(mockLogConnector).androidLog(LogLevel.VERBOSE, TAG, "Test V", throwable);
        verify(mockLogConnector).androidLog(LogLevel.DEBUG, TAG, "Test D", throwable);
        verify(mockLogConnector).androidLog(LogLevel.INFO, TAG, "Test I", throwable);
        verify(mockLogConnector).androidLog(LogLevel.WARNING, TAG, "Test W", throwable);
        verify(mockLogConnector).androidLog(LogLevel.ERROR, TAG, "Test E", throwable);
        verify(mockLogConnector).androidLog(LogLevel.WTF, TAG, "Test WTF", throwable);
    }
}
