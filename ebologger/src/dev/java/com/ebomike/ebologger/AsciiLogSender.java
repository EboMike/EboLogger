package com.ebomike.ebologger;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.ebomike.ebologger.model.ReadableLogMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Implementation of the {@link LogSender} that writes all log messages in human-readable
 * ASCII format to the provided Writer (typically a FileWriter to save the log to a file).
 *
 * <p>To create an instance of this object, create a {@link Builder} and then call
 * {@link GlobalConfig.Builder#addSender} on the result of {@link Builder#build}.
 *
 * <p>A template message template contains placeholders that will be replaced with the actual
 * properties of the message. The following placeholders are supported:
 *
 * <p>
 * <code>{date}</code> - the timestamp of the message<br>
 * <code>{severity}</code> - single-letter indicator of the severity of the message<br>
 * <code>{message}</code> - the actual log message itself
 */
public class AsciiLogSender implements LogSender {
    /** Default template if no other is provided. */
    private static final String DEFAULT_TEMPLATE = "{date} {severity} {message}\n";

    /** Default date formatter to mimick Android's date format. */
    private static final SimpleDateFormat DEFAULT_DATE_FORMAT =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);

    /**
     * The writer to write the ASCII log to. Can be null if an error occurred and we should refrain
     * from writing any more text.
     */
    @Nullable
    private Writer writer;

    /** The actual template used for the message. Should contain newline at the end. */
    private final String template;

    /** The formatter used for time stamps. */
    private final SimpleDateFormat formatter;

    private AsciiLogSender(Writer writer, String template, SimpleDateFormat formatter) {
        this.template = template;
        this.writer = writer;
        this.formatter = formatter;
    }

    @Override
    public void sendMessage(ReadableLogMessage message) {
        if (writer != null) {
            String line = expandTemplate(message);
            try {
                writer.write(line);
            } catch (IOException e) {
                // Set the writer to null so we won't try to write again.
                writer = null;
                // Send a message, the other LogSenders will process it.
                EboLogger.get(this).error().exception(e).log("Cannot write ASCII log");
            }
        }
    }

    private String expandTemplate(ReadableLogMessage message) {
        String result = template;

        result = result.replace("{date}", formatter.format(new Date(message.getTimestamp())));
        result = result.replace("{message}", message.getFormattedMessage());
        result = result.replace("{severity}", message.getSeverity().name().substring(0, 1));

        return result;
    }

    /**
     * Builder to construct a new AsciiLogSender. Most arguments are optional, but a writer must
     * be provided, either with {@link #writer} or {@link #filename}.
     */
    public static class Builder {
        private String template = DEFAULT_TEMPLATE;

        private SimpleDateFormat formatter = DEFAULT_DATE_FORMAT;

        private Writer writer = null;

        /**
         * Overrides the formatter to create timestamp strings.
         */
        public Builder formatter(SimpleDateFormat formatter) {
            this.formatter = formatter;
            return this;
        }

        /**
         * Overrides the template for log messages. See {@link AsciiLogSender} for details. Note
         * that this string should contain a newline at the end.
         */
        public Builder template(String template) {
            this.template = template;
            return this;
        }

        /**
         * Filename to write the log messages to. This is a convenience function for {@link #writer}
         * that will create a FileWriter wrapped in a BufferedWriter.
         */
        public Builder filename(String filename) throws IOException {
            return writer(new BufferedWriter(new FileWriter(filename)));
        }

        /**
         * Writer to send all ASCII log messages to.
         */
        public Builder writer(Writer writer) {
            this.writer = writer;
            return this;
        }

        /**
         * Create an actual {@link AsciiLogSender} object from all the parameters provided.
         */
        public AsciiLogSender build() {
            if (writer == null) {
                throw new IllegalArgumentException("AsciiLogSender needs a writer.");
            }

            return new AsciiLogSender(writer, template, formatter);
        }
    }
}
