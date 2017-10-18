package com.ebomike.ebologger;

import com.ebomike.ebologger.EboLogger.LogLevel;
import com.ebomike.ebologger.model.ReadableLogMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AsciiLogSenderTest {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private ByteArrayOutputStream output;

    private Writer writer;

    private ReadableLogMessage log;

    @Before
    public void setUp() {
        output = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(output, CHARSET);

        // Create a basic mocked log message, individual tests can tweak it as needed.
        log = mock(ReadableLogMessage.class);
        when(log.getTimestamp()).thenReturn(203587200123L);
        when(log.getSeverity()).thenReturn(LogLevel.WARNING);
        when(log.getFormattedMessage()).thenReturn("Test Formatted Message");

        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
    }

    @Test
    public void testDefaultTemplate() throws Exception {
        AsciiLogSender sender = new AsciiLogSender.Builder()
                .writer(writer)
                .build();

        sender.sendMessage(log);
        expectMessage("06-14 01:00:00.123 W Test Formatted Message\n");
    }

    @Test
    public void testCustomTemplate() throws Exception {
        AsciiLogSender sender = new AsciiLogSender.Builder()
                .writer(writer)
                .template("{message}@{severity}")
                .build();

        sender.sendMessage(log);
        expectMessage("Test Formatted Message@W");
    }

    @Test
    public void testCustomFormatter() throws Exception {
        AsciiLogSender sender = new AsciiLogSender.Builder()
                .writer(writer)
                .formatter(new SimpleDateFormat("MMM", Locale.US))
                .build();

        sender.sendMessage(log);
        expectMessage("Jun W Test Formatted Message\n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingWriter() throws Exception {
        // Should fail because no writer was provided.
        new AsciiLogSender.Builder().build();
    }

    private void expectMessage(String expectedMessage) throws Exception {
        writer.close();
        byte[] data = output.toByteArray();
        String actualMessage = new String(data, CHARSET);
        assertThat(actualMessage, equalTo(expectedMessage));
    }
}
