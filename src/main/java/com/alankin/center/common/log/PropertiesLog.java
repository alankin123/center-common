package com.alankin.center.common.log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesLog {
	/** 属性文件路径 */
	private final static String fileName = "businessLog.properties";
	/** 属性集 */
	private static Properties props = new Properties();
	/** 外放对象 */
	private static PropertiesLog propertys;

	/** 构造私有 */
	private PropertiesLog() {
		try {
			props.load(new InputStreamReader(PropertiesLog.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));
		} catch (Exception e) {
		}
	}

	/** 公共方法获取实例 */
	public static PropertiesLog getInstans() {
		synchronized (props) {
			if (propertys == null) {
				propertys = new PropertiesLog();
			}
		}
		return propertys;
	}

	/**
	 * 获取字符串对应值
	 * 
	 * @throws IOException
	 */
	public String getValue(String key) {
		// 取出相关值
		try {
			return props.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Map<String, String> getAllProps() {
		Map<String, String> allProp = new HashMap<String, String>();
		Set<String> propSet = props.stringPropertyNames();
		Iterator<String> iprop = propSet.iterator();// 先迭代出来
		while (iprop.hasNext()) {// 遍历
			String key = iprop.next();
			allProp.put(key, getValue(key));
		}
		return allProp;
	}

	public Iterator<String> getAllPropNames() {
		Set<String> propSet = props.stringPropertyNames();
		Iterator<String> iprop = propSet.iterator();// 先迭代出来
		return iprop;
	}

}
