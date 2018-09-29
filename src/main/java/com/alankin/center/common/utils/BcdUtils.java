package com.alankin.center.common.utils;

public class BcdUtils {

    /**
     * 字母'A'的ASCII编码值
     */
    public final static byte ALPHA_A_ASCII_VALUE = 0x41;

    /**
     * 字母'a'的ASCII编码值
     */
    public final static byte ALPHA_a_ASCII_VALUE = 0x61;

    /**
     * 数字'0'的ASCII编码值
     */
    public final static byte DIGITAL_0_ASCII_VALUE = 0x30;

    private BcdUtils() {
    }

    /**
     * 从BCD编码转换成ASCII编码.
     * 
     * @param bcdBuf
     *            , BCD编码缓冲区
     * @param asciiLen
     *            , 统一采用ASCII编码时的信息长度
     * @param rightAlignFlag
     *            , 奇数个ASCII码时采用的右对齐方式标志
     * @return, ASCII编码缓冲区
     */
    public static byte[] bcd2Ascii(byte[] bcdBuf, int bcdOffset, int asciiLen, boolean rightAlignFlag) {
        byte[] asciiBuf = new byte[asciiLen];

        int cnt;

        if (((asciiLen & 1) == 1) && rightAlignFlag) {
            cnt = 1;
            asciiLen++;
        }
        else {
            cnt = 0;
        }
        int asciiOffset = 0;
        for (; cnt < asciiLen; cnt++, asciiOffset++) {
            asciiBuf[asciiOffset] =
                (byte) ((((cnt) & 1) == 1) ? (bcdBuf[bcdOffset++] & 0x0f) : ((bcdBuf[bcdOffset] >> 4) & 0x0f));
            asciiBuf[asciiOffset] =
                (byte) (asciiBuf[asciiOffset] + ((asciiBuf[asciiOffset] > 9) ? (ALPHA_A_ASCII_VALUE - 10)
                    : DIGITAL_0_ASCII_VALUE));
        }

        return asciiBuf;
    }

    /**
     * 从BCD编码转换成ASCII编码字符串
     * 
     * @param bcdBuf
     *            , BCD编码缓冲区
     * @param asciiLen
     *            , 统一采用ASCII编码时的信息长度
     * @param rightAlignFlag
     *            , 奇数个ASCII码时采用的右对齐方式标志
     * @return, ASCII编码的字符串
     */
    public static String bcd2AsciiString(byte[] bcdBuf, int bcdOffset, int asciiLen, boolean rightAlignFlag,
        String encoding) {
        try {
            return new String(bcd2Ascii(bcdBuf, bcdOffset, asciiLen, rightAlignFlag), encoding);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 从ASCII编码转换成BCD编码.
     * 
     * @param asciiBuf
     *            , ASCII编码缓冲区
     * @param asciiOffset
     *            , ASCII编码缓冲区的起始偏移
     * @param asciiLen
     *            , 统一采用ASCII编码时的信息长度
     * @param rightAlignFlag
     *            , 奇数个ASCII码时采用的右对齐方式标志 true 右补
     * @return, BCD编码缓冲区
     */
    public static byte[] asciiString2Bcd(byte[] asciiBuf, int asciiOffset, int asciiLen, boolean rightAlignFlag) {
        byte[] bcdBuf = new byte[(asciiLen + 1) / 2];

        int cnt;
        byte ch, ch1;

        if (((asciiLen & 1) == 1) && rightAlignFlag) {
            ch1 = 0;
        }
        else {
            ch1 = 0x55;
        }

        int bcdOffset = 0;
        for (cnt = 0; cnt < asciiLen; cnt++, asciiOffset++) {
            if (asciiBuf[asciiOffset] >= ALPHA_a_ASCII_VALUE)
                ch = (byte) (asciiBuf[asciiOffset] - ALPHA_a_ASCII_VALUE + 10);
            else if (asciiBuf[asciiOffset] >= ALPHA_A_ASCII_VALUE)
                ch = (byte) (asciiBuf[asciiOffset] - ALPHA_A_ASCII_VALUE + 10);
            else if (asciiBuf[asciiOffset] >= DIGITAL_0_ASCII_VALUE)
                ch = (byte) (asciiBuf[asciiOffset] - DIGITAL_0_ASCII_VALUE);
            else
                ch = 0x00;

            if (ch1 == 0x55)
                ch1 = ch;
            else {
                bcdBuf[bcdOffset] = (byte) (ch1 << 4 | ch);
                bcdOffset++;
                ch1 = 0x55;
            }
        }

        if (ch1 != 0x55)
            bcdBuf[bcdOffset] = (byte) (ch1 << 4);

        return bcdBuf;
    }

    /**
     * 从ASCII编码转换成BCD编码.
     * 
     * @param asciiStr
     *            ascii String
     * @param asciiOffset
     *            , ASCII编码缓冲区的起始偏移
     * @param rightAlignFlag
     *            , 奇数个ASCII码时采用的右对齐方式标志 true 右补
     * @return, BCD编码缓冲区
     */
    public static byte[] asciiString2Bcd(String asciiStr, int asciiOffset, boolean rightAlignFlag, String encoding) {
        try {
            byte[] asciiBuf = asciiStr.getBytes(encoding);
            return asciiString2Bcd(asciiBuf, asciiOffset, asciiBuf.length, rightAlignFlag);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 截取字节流
     * 
     * @param bytes
     * @param length
     * @return
     */
    public static byte[] getSubBytes(byte[] Src, int offset, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = Src[offset + i];
        }
        return bytes;
    }

    public static void setBytesZero(byte[] Src, int length) {
        for (int i = 0; i < length; i++) {
            Src[i] = (byte) 0x00;
        }
    }

    public static String bytesToHexString(byte[] src, int len) {

        StringBuilder stringBuilder = new StringBuilder("");

        if (src == null || len <= 0) {
            return null;
        }

        stringBuilder.append("\n");

        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;

            String hv = Integer.toHexString(v);

            if (hv.length() < 2) {
                stringBuilder.append(0);
            }

            stringBuilder.append(hv.toUpperCase() + " ");

            if (i % 20 == 19)
                stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

}
