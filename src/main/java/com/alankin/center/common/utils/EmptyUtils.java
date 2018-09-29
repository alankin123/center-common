/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.utils;

import java.util.Collection;


/**
 * 数据校验为空工具类.<p>
 * @author lansongtao
 * @Date 2015年8月20日
 * @since 1.0
 */
public class EmptyUtils {

	/**
	 * 判断字符串是否为空<p>
	 * @param value		字符串
	 * @return			true:为空 false:不为空
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.trim().length() == 0;
	}
	
	/**
	 * 判断集合是否为空<p>
	 * @param value		集合
	 * @return			true:为空 false:不为空
	 */
	public static boolean isEmpty(Collection<?> value) {
		return value == null || value.isEmpty();
	}
	
	/**
	 * 判断字数组是否为空<p>
	 * @param value		数组
	 * @return			true:为空 false:不为空
	 */
	public static boolean isEmpty(Object[] value) {
		return value == null || value.length == 0;
	}

	/**
	 * 判断长整型是否为空<p>
	 * @param value		字符串
	 * @return			true:为空 false:不为空
	 */
	public static boolean isEmpty(Long value) {
		return value == null || value.intValue() == 0;
	}
	
	/**
	 * 判断整形是否为空<p>
	 * @param value		字符串
	 * @return			true:为空 false:不为空
	 */
	public static boolean isEmpty(Integer value) {
		return value == null || value.intValue() == 0;
	}
}
