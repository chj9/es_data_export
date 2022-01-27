package com.chenhj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;


public abstract class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    public static final int BUFFER_SIZE = 4096;
    private static final byte[] EMPTY = new byte[0];

    public StreamUtils() {
    }

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        if (in == null) {
            logger.warn("input stream is null");
            return EMPTY;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        copy(in, out);
        return out.toByteArray();
    }

    public static String copyToString(InputStream in, Charset charset) throws IOException {
        if (in == null) {
            logger.warn("input stream is null");
            return "";
        } else {
            StringBuilder out = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(in, charset);
            char[] buffer = new char[BUFFER_SIZE];

            int bytesRead;
            while((bytesRead = reader.read(buffer)) != -1) {
                out.append(buffer, 0, bytesRead);
            }

            return out.toString();
        }
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.requireNonNull(in, "No input byte array specified");
        Assert.requireNonNull(out, "No OutputStream specified");
        out.write(in);
    }

    public static void copy(String in, Charset charset, OutputStream out) throws IOException {
        Assert.requireNonNull(in, "No input String specified");
        Assert.requireNonNull(charset, "No charset specified");
        Assert.requireNonNull(out, "No OutputStream specified");
        Writer writer = new OutputStreamWriter(out, charset);
        writer.write(in);
        writer.flush();
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.requireNonNull(in, "No InputStream specified");
        Assert.requireNonNull(out, "No OutputStream specified");
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];

        int bytesRead;
        for (; (bytesRead = in.read(buffer)) != -1; byteCount += bytesRead) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        return byteCount;
    }

}
