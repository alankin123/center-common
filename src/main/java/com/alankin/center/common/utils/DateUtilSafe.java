package com.alankin.center.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期时间工具类(线程安全)
 * 
 * @author lanst
 * @Date 2016年4月7日
 * @since 1.0
 */
public class DateUtilSafe {
	public static final String dtLongs = "yyyyMMddHHmmssSSS";
	public static final String dtLong = "yyyyMMddHHmmss";
	public static final String simple = "yyyy-MM-dd HH:mm:ss";
	public static final String dtShort_ = "yyyy-MM-dd";
	public static final String dtShort = "yyyyMMdd";
	public static final String dtTime = "HHmmss";
	public static final String dtTimeLong = "HH:mm:ss";

	/** 锁对象 */
	private static final Object lockObj = new Object();

	/** 存放不同的日期模板格式的sdf的Map */
	private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	/**
	 * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
	 * 
	 * @param pattern
	 * @return
	 */
	public static SimpleDateFormat getSdf(final String pattern) {
		ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

		// 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
		if (tl == null) {
			synchronized (lockObj) {
				tl = sdfMap.get(pattern);
				if (tl == null) {
					// 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
					// 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
					tl = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected SimpleDateFormat initialValue() {
							return new SimpleDateFormat(pattern);
						}
					};
					sdfMap.put(pattern, tl);
				}
			}
		}

		return tl.get();
	}

	/**
	 * 获取日期字符串
	 * 
	 * @param date
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static String format(Date date, String pattern) {
		return getSdf(pattern).format(date);
	}

	/**
	 * 获取日期
	 * 
	 * @param dateStr
	 * @param pattern
	 *            日期格式
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String dateStr, String pattern) throws ParseException {
		return getSdf(pattern).parse(dateStr);
	}

	/**
	 * 获取当前日期时间，格式： yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		return format(new Date(), dtLong);
	}

	/**
	 * 转换为Date，格式： yyyyMMddHHmmss
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateTime(String dateStr) throws ParseException {
		return parse(dateStr, dtLong);
	}

	/**
	 * 获取当前日期，格式： yyyyMMdd
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		return format(new Date(), dtShort);
	}

	/**
	 * 转换为Date，格式： yyyyMMdd
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String dateStr) throws ParseException {
		return parse(dateStr, dtShort);
	}

	/**
	 * 转换为Date，格式： yyyyMMdd
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateAll(String dateStr) {
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		try {
			if (dateStr.length() == dtLong.length()) {
				return parse(dateStr, dtLong);
			}
			if (dateStr.length() == simple.length()) {
				return parse(dateStr, simple);
			}
			if (dateStr.length() == dtShort_.length()) {
				return parse(dateStr, dtShort_);
			}
			if (dateStr.length() == dtShort.length()) {
				return parse(dateStr, dtShort);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取当前时间，格式： HHmmss
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		return format(new Date(), dtTime);
	}

	/**
	 * 转换为Date，格式： HHmmss
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseTime(String dateStr) throws ParseException {
		return parse(dateStr, dtTime);
	}

	/**
	 * 转换为Date，格式： HHmmss
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static LocalTime parseLocalTime(String dateStr) throws ParseException {
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		int hour;
		int minute;
		int second;

		if (dtTime.length() == dateStr.length()) {
			hour = Integer.parseInt(dateStr.substring(0, 2));
			minute = Integer.parseInt(dateStr.substring(2, 4));
			second = Integer.parseInt(dateStr.substring(4, 6));
			return LocalTime.of(hour, minute, second);
		}

		if (dtTimeLong.length() == dateStr.length() && dateStr.indexOf(":") > -1) {
			String[] args = dateStr.split(":");
			hour = Integer.parseInt(args[0]);
			minute = Integer.parseInt(args[1]);
			second = Integer.parseInt(args[2]);
			return LocalTime.of(hour, minute, second);
		}

		return null;
	}

}
