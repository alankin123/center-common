package com.alankin.center.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 字符串工具类.
 * 
 * @author lansongtao
 * @Date 2015年8月3日
 * @since 1.0
 */
public class StringUtils {
	public static final String DEFAULT_ENCODING = "UTF-8";

	@SuppressWarnings("unused")
	private static final String[][] sqlhandles = { { "'", "''" }, { "\\\\", "\\\\\\\\" } };

	public static String subString(String str, int beginIndex, int endIndex) {
		return str.substring(beginIndex, endIndex);
	}

	/**
	 * 判断字符串是否为空.
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static boolean isEmpty(Object str) {
		return str == null || str.toString().trim().length() == 0 || str.toString().trim().equals("null")  || str.toString().trim().equals("undefined");
	}

	public static boolean isNotEmpty(Object str) {
		return str != null && str.toString().trim().length() > 0 && !str.toString().trim().equals("null") && !str.toString().trim().equals("undefined");
	}

	/**
	 * 转换对象为字符串.
	 * <p>
	 * 如果是非String类型,则转换为Json对象或String对象
	 * 
	 * @param object
	 *            对象
	 * @return 转换后字符串
	 */
	public static String convertString(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof String) {
			return (String) object;
		} else {
			try {
				return JsonUtils.convert(object);
			} catch (Exception e) {
				return ToStringBuilder.reflectionToString(object);
			}
		}
	}

	/**
	 * 如果是str1为空,则返回str2;否则返回str1
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String snull(String str1, String str2) {
		if (isEmpty(str1)) {
			return str2;
		}
		return str1;
	}

	/**
	 * 
	 * 转换map里面的key为小写
	 * 
	 * @param map
	 * @return
	 * @author pengjinhui
	 * @since 2015年12月14日上午11:12:45
	 */
	public static Map<String, Object> converLowerKey(Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			resultMap.put(entry.getKey().toLowerCase(), entry.getValue());
		}
		return resultMap;

	}

	/**
	 * 
	 * 首字母大写
	 * 
	 * @param name
	 * @return
	 * @author pengjinhui
	 * @since 2015年12月30日下午2:05:53
	 */
	public static String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * 
	 * 字符串转list,以逗号隔开
	 * 
	 * @param str
	 * @return
	 * @author pengjinhui
	 * @since 2016年1月18日下午1:46:14
	 */
	public static List<String> splitStr(String str) {
		if (StringUtils.isEmpty(str))
			return null;
		String[] strs = str.split(",");
		List<String> list = new ArrayList<String>();
		for (String id : strs) {
			if (!StringUtils.isEmpty(id)) {
				list.add(id);
			}
		}
		return list;
	}

	/**
	 * 
	 * 数据转字符串
	 * 
	 * @param strs
	 * @return
	 * @author pengjinhui
	 * @since 2016年1月26日下午5:23:57
	 */
	public static String arrayToStr(String[] strs) {
		if (strs == null || strs.length == 0)
			return null;
		StringBuffer sb = new StringBuffer();
		for (String id : strs) {
			if (!StringUtils.isEmpty(id)) {
				sb.append(",").append("'").append(id).append("'");
			}
		}
		if (sb.length() > 0) {
			return sb.substring(1);
		}
		return null;
	}

	public static boolean isExistArray(String str, String[] arrays) {
		if (arrays == null || arrays.length == 0) {
			return false;
		}
		for (String cont : arrays) {
			if (cont.equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * Description: 获取该数据区的数据格式
	 * 
	 * @author:
	 * @date: 2014年7月18日
	 * @param value
	 * @return
	 */
	public static String getFormatValue(int length, boolean isLeftAlign, String value, String format, String fixTag) throws Exception {
		try {
			if (length > 0 && new String(value.getBytes(format), "iso-8859-1").length() > length) {
				throw new Exception("需要填充数据长度必须大于数据值长度");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("编码错误");
		}
		String formatValue = null;
		if (length > 0) {
			formatValue = supplyValue(length, isLeftAlign, value, fixTag, format);
		} else {
			formatValue = value;
		}
		return formatValue;
	}

	/**
	 * 
	 * Description: 填充指定长度的数据
	 * 
	 * @author:
	 * @date: 2014年7月18日
	 * @param length
	 * @param isLeftAlign
	 *            是否左对齐
	 * @param value
	 *            填充数据
	 * @param fixTag
	 *            填充标签
	 * @return
	 */
	public static String supplyValue(int length, boolean isLeftAlign, String value, String fixTag, String sourceFormat) throws Exception {
		String result = "";
		String afterFormat = "utf-8";
		if (sourceFormat != null && !"".equals(sourceFormat.trim())) {
			afterFormat = sourceFormat;
		}
		String beforeFormat = "iso-8859-1";

		String format = null;
		if (isLeftAlign) {
			format = "%-" + length + "s";
		} else {
			format = "%" + length + "s";
		}
		try {
			if (isChinese(value)) {
				result = String.format(format, new String(value.getBytes(afterFormat), beforeFormat));
				result = new String(result.getBytes(beforeFormat), afterFormat);
			} else {
				result = String.format(format, value);
			}
			if (!" ".equals(fixTag) && !"".equals(fixTag)) {
				result = result.replaceAll(" ", fixTag);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}

	// 完整的判断中文汉字和符号
	private static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
		        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
		        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
		        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	public static String convNull(byte[] values) {
		if (values == null) {
			return "";
		}
		return new String(values);
	}

	public static String convNull(String values) {
		if (StringUtils.isEmpty(values)) {
			return "";
		}
		return values.trim();
	}

	/**
	 * 
	 * 功能描述 按UTF-8格式截取指定长度的字符串
	 * 
	 * @param value
	 * @param length
	 * @return
	 * @author WillYang
	 * @throws UnsupportedEncodingException
	 * @since 2016年1月18日下午8:40:27
	 */
	public static String subValue(String value, int length) {
		try {
			return subValue(value, length, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 
	 * 功能描述 按指定格式截取指定长度的字符串
	 * 
	 * @param value
	 * @param length
	 * @param encoding
	 * @return
	 * @author WillYang
	 * @since 2016年1月18日下午8:41:01
	 */
	public static String subValue(String value, int length, String encoding) throws UnsupportedEncodingException {
		String useValue = value;
		if (!checkLength(useValue, encoding, length)) {
			return useValue;
		}
		int totalLen = 0;
		String chinese = "我";
		String character = "A";
		int chineseLen = chinese.getBytes(encoding).length;
		int characterLen = character.getBytes(encoding).length;
		int index = 0;
		while (index < useValue.length()) {
			char charValue = useValue.charAt(index);
			totalLen += calCharLength(charValue, chineseLen, characterLen);
			if (totalLen >= length) {
				break;
			}
			index++;
		}
		if (totalLen == length) {
			useValue = useValue.substring(0, ++index);
		} else {
			useValue = useValue.substring(0, index);
		}
		totalLen = useValue.getBytes(encoding).length;
		if (totalLen > length) {
			useValue = useValue.substring(0, --index);
		}
		return useValue;
	}

	private static boolean checkLength(String value, String encoding, int length) throws UnsupportedEncodingException {
		if (value == null) {
			return false;
		}
		int valueLen = value.getBytes(encoding).length;
		if (valueLen <= length) {
			return false;
		}
		return true;
	}

	private static int calCharLength(char value, int chineseLen, int characterLen) {
		if (isChinese(value)) {
			return chineseLen;
		} else {
			return characterLen;
		}
	}
}