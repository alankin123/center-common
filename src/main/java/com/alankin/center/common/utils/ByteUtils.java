package com.alankin.center.common.utils;

public class ByteUtils {

	private ByteUtils() {
	}

	/***
	 * 将int转换成高低两个字节
	 * 
	 * @param src
	 *            最大值 127*256+127
	 * @return
	 */
	public static byte[] intToHLbyte(int src) {
		byte[] result = new byte[2];
		result[0] = (byte) (src / 256);
		result[1] = (byte) (src % 256);
		return result;
	}

	/***
	 * 将int转换成低高两个字节
	 * 
	 * @param src
	 *            最大值 127*256+127
	 * @return
	 */
	public static byte[] intToLHbyte(int src) {
		byte[] result = new byte[2];
		result[0] = (byte) (src % 256);
		result[1] = (byte) (src / 256);
		return result;
	}

	/***
	 * 将两个byte高低位字节还原成int类型
	 * 
	 * @param high
	 * @param low
	 * @return
	 */
	public static int hLbyteToInt(int high, int low) {
		return high * 256 + low;
	}

	public static int hLbyteToInt(byte high, byte low) {
		int higghtPostion = high;
		int lowpostion = low;
		if (high < 0) {
			higghtPostion = toUnsignedByte(high);
		}
		if (low < 0) {
			lowpostion = toUnsignedByte(low);
		}
		return hLbyteToInt(higghtPostion, lowpostion);
	}

	/**
	 * 将二进制字符串转换为byte数组
	 * 
	 * @param binaryStringBuilder
	 *            二进制字符串序列
	 * @return byte[] 转换后的byte数组
	 */
	public static byte[] binaryString2Bytes(StringBuilder binaryStringBuilder) {
		StringBuffer temp = new StringBuffer();
		int length = binaryStringBuilder.length();
		byte[] result = new byte[length / 8];

		int tempVar = 0;
		int r = 0;
		for (int i = 0; i < length; i++) {
			temp.append(binaryStringBuilder.charAt(i));
			if ((i + 1) % 8 == 0) {
				r = (i + 1) / 8 - 1;
				tempVar = Integer.parseInt(temp.toString(), 2);
				result[r] = (byte) tempVar;
				temp.delete(0, temp.length());
			}
		}
		return result;
	}

	/**
	 * 把byte数组转化成2进制字符串
	 * 
	 * @param bArr
	 * @return
	 */
	public static String bytes2BinaryString(byte... bArr) {
		StringBuilder result = new StringBuilder();
		StringBuilder temp = null;
		for (byte b : bArr) {
			temp = new StringBuilder();
			byte a = b;
			for (int i = 0; i < 8; i++) {
				byte c = a;
				a = (byte) (a >> 1);// 每移一位如同将10进制数除以2并去掉余数。
				a = (byte) (a << 1);
				if (a == c) {
					temp.insert(0, "0");
					// result = "0" + result;
				} else {
					temp.insert(0, "1");
					// result = "1" + result;
				}
				a = (byte) (a >> 1);
			}
			result.append(temp);
		}
		return result.toString();
	}

	/***
	 * 判断某个字节数组串是否全为数值
	 * 
	 * @param src
	 * @return true 全为数值
	 */
	public static boolean isAllNum(byte[] src) {
		if (src == null || src.length == 0) {
			return false;
		}

		for (int i = 0; i < src.length; i++) {
			if (src[i] < 0x30 || src[i] > 0x39)// 不再0-9范围
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * 讲字节流转换为16进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexString(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString().toUpperCase();
	}

	/**
	 * 将16进制字符串转换为字节流
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || "".equals(hexString.trim())) {
			return null;
		}
		if (hexString.length() % 2 == 1) {
			hexString = "0" + hexString;
		}
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toUpperCase().toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 将有符号的byte转成int
	 * 
	 * @param unsigned
	 * @return
	 */
	public static int toUnsignedByte(byte sige) {
		return sige & 0xff;
	}

	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	/**
	 * 
	 * Description: 截取定长的Byte数组获取定长的
	 * 
	 * @author:
	 * @date: 2014年6月18日
	 * @param byteArray
	 * @param offset
	 *            数组起始位置
	 * @param len
	 * @return
	 */
	public static byte[] subByteArray(byte[] byteArray, int offset, int len) {
		if (byteArray == null || (byteArray.length - offset) < len) {
			return null;
		}
		byte[] newByte = new byte[len];
		for (int i = 0; i < len; i++, offset++) {
			newByte[i] = byteArray[offset];
		}
		return newByte;
	}

	/**
	 * 
	 * 功能描述 CRC
	 * 
	 * @param packData
	 * @return
	 * @author WillYang
	 * @since 2015年9月30日下午12:49:42
	 */
	public static byte getCrcData(byte[] packData) {
		byte crc = packData[0];
		for (int i = 1; i < packData.length; i++) {
			crc = (byte) (crc ^ packData[i]);
		}
		return crc;
	}

	/**
	 * 
	 * 功能描述
	 * 
	 * @param n
	 * @return
	 * @author WillYang
	 * @since 2015年9月30日下午12:51:17
	 */
	public static byte[] shortToBytes(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) ((n >> 8) & 0xff);
		return b;
	}

	/**
	 * 指定位置插入byte数组
	 * 
	 * @param dst
	 * @param dstPos
	 *            放入的位置
	 * @param src
	 * @param inPutLength
	 *            要放入的长度
	 */
	public static byte[] inPutByte(byte[] dst, int dstPos, byte[] src, int inPutLength) {
		for (int i = 0; i < inPutLength; i++) {
			dst[dstPos + i] = src[i];
		}
		return dst;
	}
}
