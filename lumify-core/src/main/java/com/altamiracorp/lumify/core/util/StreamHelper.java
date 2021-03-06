package com.altamiracorp.lumify.core.util;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Helper class to handle Runtime.exec() output.
 */
public class StreamHelper extends Thread {

    private InputStream inputStream;
    private OutputStream outputStream;
    protected StringBuffer contentBuffer = null;

    protected String prefix = null;

    /**
     * the output writer
     */
    protected PrintWriter writer = null;

    /**
     * Append messages to this logger
     */
    protected LumifyLogger logger = null;

    /**
     * True to keep reading the streams
     */
    boolean keepReading = true;

    /**
     * Creates a new stream helper and immediately starts capturing output from
     * the given stream.
     *
     * @param inputStream the input stream
     */
    public StreamHelper(InputStream inputStream) {
        this(inputStream, null, null, null, null);
    }

    /**
     * Creates a new stream helper and immediately starts capturing output from
     * the given stream. Output will be captured to the given buffer.
     *
     * @param inputStream   the input stream to read from
     * @param contentBuffer the buffer to write the captured output to
     */
    public StreamHelper(InputStream inputStream, StringBuffer contentBuffer) {
        this(inputStream, null, null, contentBuffer, null);
    }

    /**
     * Creates a new stream helper and immediately starts capturing output from
     * the given stream. Output will be captured to the given buffer.
     *
     * @param inputStream   the input stream to read from
     * @param logger        the logger to append to
     * @param contentBuffer the buffer to write the captured output to
     */
    public StreamHelper(InputStream inputStream, LumifyLogger logger,
                        StringBuffer contentBuffer) {
        this(inputStream, null, logger, contentBuffer, null);
    }

    public StreamHelper(InputStream inputStream, LumifyLogger logger, String prefix) {
        this(inputStream, null, logger, null, prefix);
    }

    /**
     * Creates a new stream helper and immediately starts capturing output from
     * the given stream. Output will be captured to the given buffer and also
     * redirected to the provided output stream.
     *
     * @param inputStream   the input stream to read from
     * @param redirect      a stream to also redirect the captured output to
     * @param contentBuffer the buffer to write the captured output to
     */
    public StreamHelper(InputStream inputStream, OutputStream redirect,
                        StringBuffer contentBuffer) {
        this(inputStream, redirect, null, contentBuffer, null);
    }

    /**
     * Creates a new stream helper and immediately starts capturing output from
     * the given stream. Output will be captured to the given buffer and also
     * redirected to the provided output stream.
     *
     * @param inputStream   the input stream to read from
     * @param redirect      a stream to also redirect the captured output to
     * @param logger        the logger to append to
     * @param contentBuffer the buffer to write the captured output to
     */
    public StreamHelper(InputStream inputStream, OutputStream redirect,
                        LumifyLogger logger, StringBuffer contentBuffer, String prefix) {
        this.inputStream = inputStream;
        this.outputStream = redirect;
        this.logger = logger;
        this.contentBuffer = contentBuffer;
        this.prefix = prefix;
    }

    /**
     * Tells the stream helper to stop reading and exit from the main loop.
     */
    public void stopReading() {
        keepReading = false;
    }

    /**
     * Thread run
     */
    @Override
    public void run() {
        BufferedReader reader = null;
        InputStreamReader isreader = null;
        try {
            if (outputStream != null) {
                writer = new PrintWriter(outputStream);
            }
            isreader = new InputStreamReader(inputStream);
            reader = new BufferedReader(isreader);
            String line = null;
            while (keepReading && (line = reader.readLine()) != null) {
                if (prefix != null) {
                    line = prefix + line;
                }
                append(line);
                log(line);
            }
            if (writer != null)
                writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(isreader);
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * This method will write any output from the stream to the the content buffer
     * and the logger.
     *
     * @param output the stream output
     */
    protected void append(String output) {
        // Process stream redirects
        if (writer != null) {
            writer.println(output);
        }

        // Fill the content buffer, if one has been assigned
        if (contentBuffer != null) {
            contentBuffer.append(output.trim());
            contentBuffer.append('\n');
        }

        // Append output to logger?
    }

    /**
     * If a logger has been specified, the output is written to the logger using
     * the defined log level.
     *
     * @param output the stream output
     */
    protected void log(String output) {
        if (logger != null) {
            logger.info("%s", output);
        }
    }
}
