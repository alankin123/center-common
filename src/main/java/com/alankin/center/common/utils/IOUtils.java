package com.alankin.center.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>IO工具类
 * Company: Shenzhen XGD SoftWare
 * </p>
 * @since 2015年7月28日
 * @version 1.0
 * @author WillYang
 */
public class IOUtils {

    public static final String LINE_SEPARATOR = "\r\n";

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final int BUFFER_SIZE = 1024;

    private static final String UTF8 = "utf-8";

    private IOUtils() {

    }

    public static byte[] readByteFile(String filePath) throws IOException {
        return readByteFile(new File(filePath));
    }

    public static byte[] readByteFile(File file) throws IOException {
        InputStream inputStream = null;
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("The file is not exists or is a directory. File:"
                + file.getAbsolutePath());
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] readBytes = new byte[BUFFER_SIZE];
            int readLen = -1;
            inputStream = new UnicodeInputStream(new FileInputStream(file));
            while ((readLen = inputStream.read(readBytes)) != -1) {
                outputStream.write(readBytes, 0, readLen);
            }
            return outputStream.toByteArray();
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (Exception e) {

            }
        }
    }

    public static StringBuilder readFile(String filePath) throws IOException {
        File file = new File(filePath);
        return readFile(file, UTF8);
    }

    public static StringBuilder readFile(File file, String encoding) throws IOException {
        StringBuilder builder = null;
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("The file is not exists or is a directory. File:"
                + file.getAbsolutePath());
        }
        BufferedReader reader = null;
        try {
            UnicodeInputStream inputStream = new UnicodeInputStream(new FileInputStream(file), encoding);
            reader = new BufferedReader(new InputStreamReader(inputStream, inputStream.getEncoding()));
            builder = new StringBuilder();
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                builder.append(readLine + LINE_SEPARATOR);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {

            }
        }
        return builder;
    }

    public static void writeFile(String filePath, StringBuilder builder) throws IOException {
        if (filePath == null || "".equals(filePath.trim())) {
            throw new IllegalArgumentException("The Argument filePath can not be null.");
        }
        if (builder == null) {
            throw new IllegalArgumentException("The Argument builder can not be null.");
        }
        File file = new File(filePath);
        InputStream inputStream = new ByteArrayInputStream(builder.toString().getBytes());
        writeFile(file, inputStream);
    }

    public static void writeFile(String filePath, StringBuilder builder, String encoding) throws IOException {
        if (filePath == null || "".equals(filePath.trim())) {
            throw new IllegalArgumentException("The Argument filePath can not be null.");
        }
        if (builder == null) {
            throw new IllegalArgumentException("The Argument builder can not be null.");
        }
        String useEncoding = encoding;
        if (useEncoding == null || "".equals(useEncoding.trim())) {
            useEncoding = UTF8;
        }
        File file = new File(filePath);
        InputStream inputStream = new ByteArrayInputStream(builder.toString().getBytes(encoding));
        writeFile(file, inputStream);
    }

    public static void writeFile(String filePath, byte[] writeBytes) throws IOException {
        writeFile(new File(filePath), writeBytes);
    }

    public static void writeFile(File file, byte[] writeBytes) throws IOException {
        OutputStream outputStream = null;
        try {
            if (file == null) {
                throw new IllegalArgumentException("The Argument file can not be null.");
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(writeBytes);
            outputStream.flush();
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (Exception e) {
            }
        }
    }

    public static void writeFile(File file, InputStream inputStream) throws IOException {
        OutputStream outputStream = null;
        try {
            if (file == null) {
                throw new IllegalArgumentException("The Argument file can not be null.");
            }
            if (inputStream == null) {
                throw new IllegalArgumentException("The Argument inputStream can not be null.");
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            outputStream = new FileOutputStream(file);
            int readLen = -1;
            byte[] readBytes = new byte[BUFFER_SIZE];
            while ((readLen = inputStream.read(readBytes)) != -1) {
                outputStream.write(readBytes, 0, readLen);
            }
            outputStream.flush();
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (Exception e) {

            }
        }
    }

    /**
     * version: 1.1 / 2007-01-25 - changed BOM recognition ordering (longer boms first)
     * 
     * Original pseudocode : Thomas Weidenfeller Implementation tweaked: Aki Nieminen
     * 
     * http://www.unicode.org/unicode/faq/utf_bom.html BOMs in byte length ordering: 00 00 FE FF = UTF-32, big-endian FF FE 00 00 = UTF-32, little-endian
     * EF BB BF = UTF-8, FE FF = UTF-16, big-endian FF FE = UTF-16, little-endian
     * 
     * Win2k Notepad: Unicode format = UTF-16LE
     ***/

    /**
     * This inputstream will recognize unicode BOM marks and will skip bytes if getEncoding() method is called before any of the read(...) methods.
     *
     * Usage pattern: String enc = "ISO-8859-1"; // or NULL to use systemdefault FileInputStream fis = new FileInputStream(file); UnicodeInputStream uin =
     * new UnicodeInputStream(fis, enc); enc = uin.getEncoding(); // check and skip possible BOM bytes InputStreamReader in; if (enc == null) in = new
     * InputStreamReader(uin); else in = new InputStreamReader(uin, enc);
     */
    /**
     * Read-ahead four bytes and check for BOM marks. Extra bytes are unread back to the stream, only BOM bytes are skipped.
     */
    // Unicode BOM mark not found, unread all bytes
    // System.out.println("read=" + n + ", unread=" + unread);
    // init();
    // init();
    /**
     * version: 1.1 / 2007-01-25 - changed BOM recognition ordering (longer boms first)
     * 
     * Original pseudocode : Thomas Weidenfeller Implementation tweaked: Aki Nieminen
     * 
     * http://www.unicode.org/unicode/faq/utf_bom.html BOMs: 00 00 FE FF = UTF-32, big-endian FF FE 00 00 = UTF-32, little-endian EF BB BF = UTF-8, FE FF
     * = UTF-16, big-endian FF FE = UTF-16, little-endian
     * 
     * Win2k Notepad: Unicode format = UTF-16LE
     ***/
    /**
     * This inputstream will recognize unicode BOM marks
     * and will skip bytes if getEncoding() method is called
     * before any of the read(...) methods.
     *
     * Usage pattern:
         String enc = "ISO-8859-1"; // or NULL to use systemdefault
         FileInputStream fis = new FileInputStream(file);
         UnicodeInputStream uin = new UnicodeInputStream(fis, enc);
         enc = uin.getEncoding(); // check and skip possible BOM bytes
         InputStreamReader in;
         if (enc == null) in = new InputStreamReader(uin);
         else in = new InputStreamReader(uin, enc);
     */
    private static class UnicodeInputStream extends InputStream {

        private static final int BOM_SIZE = 4;

        PushbackInputStream internalIn;

        boolean isInited = false;

        String encoding;

        String defaultEnc;

        UnicodeInputStream(InputStream in) {
            internalIn = new PushbackInputStream(in, BOM_SIZE);
            this.defaultEnc = System.getProperty("file.encoding");
        }

        UnicodeInputStream(InputStream in, String defaultEncoding) {
            internalIn = new PushbackInputStream(in, BOM_SIZE);
            this.defaultEnc = defaultEncoding;
        }

        public String getEncoding() {
            if (!isInited) {
                try {
                    init();
                }
                catch (IOException ex) {
                    IllegalStateException ise = new IllegalStateException("Init method failed.");
                    ise.initCause(ise);
                    throw ise;
                }
            }
            return encoding;
        }

        /**
         * Read-ahead four bytes and check for BOM marks. Extra bytes are
         * unread back to the stream, only BOM bytes are skipped.
         */
        protected void init() throws IOException {
            if (isInited)
                return;

            byte bom[] = new byte[BOM_SIZE];
            int n, unread;
            n = internalIn.read(bom, 0, bom.length);

            if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
                && (bom[3] == (byte) 0xFF)) {
                encoding = "UTF-32BE";
                unread = n - 4;
            }
            else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
                && (bom[3] == (byte) 0x00)) {
                encoding = "UTF-32LE";
                unread = n - 4;
            }
            else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
                encoding = "UTF-8";
                unread = n - 3;
            }
            else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
                encoding = "UTF-16BE";
                unread = n - 2;
            }
            else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
                encoding = "UTF-16LE";
                unread = n - 2;
            }
            else {
                // Unicode BOM mark not found, unread all bytes
                encoding = defaultEnc;
                unread = n;
            }

            if (unread > 0)
                internalIn.unread(bom, (n - unread), unread);

            isInited = true;
        }

        public void close() throws IOException {
            init();
            internalIn.close();
        }

        public int read() throws IOException {
            init();
            return internalIn.read();
        }
    }

}
