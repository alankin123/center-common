package com.alankin.center.common.utils;

import java.util.Arrays;
import java.util.List;


/**
 * 
 * @author lansongtao
 * 
 */
public class StringUtil
{

	/**
	 * 删掉最后一个换行字符
	 * 
	 * @param str
	 * @return
	 */
	public static String deleteLastChageLineChar(final String str)
	{
		final char last = str.charAt(str.length() - 1);
		if ('\n' == last)
		{
			final StringBuilder sb = new StringBuilder(str);
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		else
		{
			return str;
		}

	}

	/**
	 * 头字母小写
	 * 
	 * @param name
	 * @return
	 */
	public static String firstCharLowCase(final String name)
	{
		return name.substring(0, 1).toLowerCase().concat(name.substring(1));
	}

	/**
	 * 头字母大写
	 * 
	 * @param name
	 * @return
	 */
	public static String firstCharUpperCase(final String name)
	{
		return name.substring(0, 1).toUpperCase().concat(name.substring(1));
	}

	/***
	 * 合并所有的字节数组
	 * 
	 * @param first
	 * @param rest
	 * @return
	 */
	public static byte[] concatAll(byte[] first, byte[]... rest)
	{
		int totalLength = first.length;
		for (byte[] array : rest)
		{	
			if(array==null || array.length==0){
				continue;
			}
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest)
		{
			if(array==null || array.length==0){
				continue;
			}
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/***
	 * 合并所有的字节数组
	 * 
	 * @param first
	 * @param rest
	 * @return
	 */
	public static byte[] concatAll(byte[] first, List<byte[]> rest)
	{
		int totalLength = first.length;
		for (byte[] array : rest)
		{
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest)
		{
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/***
	 * 判断某个字符串是否全为数值
	 * 
	 * @param src
	 * @return true 全为数值
	 */
	public static boolean isAllNum(String src)
	{
		if (src == null || src.length() == 0)
		{
			return false;
		}
		for (int i = 0; i < src.length(); i++)
		{
			char c = src.charAt(i);
			if (c < 0x30 || c > 0x39)// 不再0-9范围
			{
				return false;
			}
		}
		return true;
	}

	/***
	 * 产生指定长度的码,数值左补0
	 * 
	 * @param srcStr
	 * @param length
	 * @param allNum
	 *            true纯数值
	 * @return
	 * @throws Exception 
	 */
	public static String supplyCode(String srcStr, int length, boolean allNum) throws Exception
	{
		String result = srcStr;
		if (srcStr.length() < length)
		{
			for (int i = 0; i < length - srcStr.length(); i++)
			{
				if (allNum)
				{// 纯数值左补0
					result = "0".concat(result);
				}
				else
				{// 纯字母右补" "
					result += " ";
				}
			}
		}
		else if (srcStr.length() > length)
		{
			throw new Exception("srcStr length more than param_length");
		}
		return result;
	}

	/***
	 * 主要解决 byte [] sb=new byte[]{50,49,51,49,50,51,50,49,-51,49,50,51}; String
	 * str=new String(sb,"UTF-8"); byte [] result=str.getBytes("UTF-8");
	 * result成14个字节的问题
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] getBytes(String str)
	{
		return null;
	}

	public static String getStacktTrace(StackTraceElement[] trace)
	{
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < trace.length; i++)
		{
			sb.append("\tat ").append(trace[i]).append("\n");
		}
		return sb.toString();
	}
}
