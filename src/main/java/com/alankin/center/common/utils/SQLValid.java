package com.alankin.center.common.utils;

import java.util.regex.Pattern;

public class SQLValid {
	static String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
	        + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

	static Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);

	/**
	 * 校验字符串中是否含有SQL注入风险
	 * 
	 * @param str
	 * 
	 * @throws Exception
	 */
	public static boolean isValid(String str) {
		if (sqlPattern.matcher(str).find()) {
			return false;
		} else {
			return true;
		}
	}

}
