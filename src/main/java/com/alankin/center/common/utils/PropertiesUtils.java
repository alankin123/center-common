package com.alankin.center.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * <p> Properties文件处理工具类
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: Shenzhen XGD SoftWare
 * </p>
 * @since 2015年7月22日
 * @version 1.0
 * @author WillYang
 */
public class PropertiesUtils
{
    private PropertiesUtils()
    {
    }

    /**
     * 
     * 功能描述
     * 根据路径获取Properties
     * @param relativePath
     * @return
     * @throws Exception
     * @author WillYang
     * @since 2015年7月23日上午8:33:59
     */
    public static Properties getProperties(String relativePath) throws Exception
    {
        return getProperties(relativePath, null);
    }

    /**
     * 
     * 功能描述
     * 根据路径按指定编码获取Properties
     * <p>
     * 编码为空则使用字节流读取文件
     * 优先从类路径加载资源,如果加载不到则使用文件系统加载资源
     * 如果文件编码为空,则使用字节流加载资源，否则用指定编码加载资源
     * </p>
     * @param relativePath 文件路径, 优先从类路径加载资源,如果加载不到则使用文件系统加载资源
     * @param encoding 文件编码 如果文件编码为空,则使用字节流加载资源，否则用指定编码加载资源
     * @return 属性文件
     * @throws Exception 当资源为空时抛出异常,当指定编码字符集不存在时抛出异常
     * @author WillYang
     * @since 2015年7月23日上午8:33:28
     */
    public static Properties getProperties(String relativePath, String encoding) throws Exception
    {
        if (relativePath == null || "".equals(relativePath.trim()))
        {
            throw new IllegalArgumentException("The file [" + relativePath + "] is not a vaild file.");
        }
        Properties properties = null;
        URL url = ResourcesUtils.getResources(relativePath);
        if (url == null)
        {
            throw new IllegalArgumentException("The file [" + relativePath + "] not exists.");
        }
        properties = new Properties();
        if (encoding != null && !"".equals(encoding.trim()))
        {
            properties.load(new BufferedReader(new InputStreamReader(url.openStream(), encoding)));
        }
        else
        {
            properties.load(url.openStream());
        }
        return properties;
    }

    /**
     * 
     * 功能描述
     * 获取指定的属性值
     * @param properties 属性集,如果为空则从系统属性获取
     * @param propName 属性名
     * @param allowEmpty 是否允许为空,如果为不允许且属性值为空则抛出异常
     * @throws IllegalArgumentException 当属性值不允许为空时
     * @return 属性值
     * @author WillYang
     * @since 2015年7月23日上午9:10:08
     */
    public static long getLongProp(Properties properties, String propName, boolean allowEmpty)
    {
        String value = getProp(properties, propName, allowEmpty);
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("The " + propName + " is not a long.");
        }
    }

    /**
     * 
     * 功能描述
     * 获取指定的属性值
     * @param properties 属性集,如果为空则从系统属性获取
     * @param propName 属性名
     * @param allowEmpty 是否允许为空,如果为不允许且属性值为空则抛出异常
     * @throws IllegalArgumentException 当属性值不允许为空时
     * @return 属性值
     * @author WillYang
     * @since 2015年7月23日上午9:12:49
     */
    public static int getIntProp(Properties properties, String propName, boolean allowEmpty)
    {
        String value = getProp(properties, propName, allowEmpty);
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("The " + propName + " is not a integer.");
        }
    }

    /**
     * 
     * 功能描述
     * 获取指定的属性值
     * @param properties 属性集,如果为空则从系统属性获取
     * @param propName 属性名
     * @param allowEmpty 是否允许为空,如果为不允许且属性值为空则抛出异常
     * @throws IllegalArgumentException 当属性值不允许为空时
     * @return 属性值
     * @author WillYang
     * @since 2015年7月23日上午9:13:06
     */
    public static String getProp(Properties properties, String propName, boolean allowEmpty)
    {
        String value = getProp(properties, propName, null);
        if (!allowEmpty && isEmpty(value))
        {
            throw new IllegalArgumentException("The " + propName + " is null.");
        }
        return value;
    }

    /**
     * 
     * 功能描述
     * 获取指定属性值
     * @param properties 属性集,如果为空则从系统属性获取
     * @param propName 属性名
     * @param defaultValue 默认值, 如果属性值为空时则返回默认值
     * @throws IllegalArgumentException 当属性值不为Long时
     * @return
     * @author WillYang
     * @since 2015年7月23日上午9:13:26
     */
    public static long getLongProp(Properties properties, String propName, long defaultValue)
    {
        String value = getProp(properties, propName, null);
        if (isEmpty(value))
        {
            return defaultValue;
        }
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("The " + propName + " is not a long.");
        }
    }

    /**
     * 
     * 功能描述
     * 获取指定属性值
     * @param properties 属性集,如果为空则从系统属性获取
     * @param propName 属性名
     * @param defaultValue 默认值, 如果属性值为空时则返回默认值
     * @throws IllegalArgumentException 当属性值不为Int时
     * @return
     * @author WillYang
     * @since 2015年7月23日上午9:16:39
     */
    public static int getIntProp(Properties properties, String propName, int defaultValue)
    {
        String value = getProp(properties, propName, null);
        if (isEmpty(value))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("The " + propName + " is not a integer.");
        }
    }

    /**
     * 
     * 功能描述
     * 获取指定属性值
     * @param properties 属性集,如果为空则从系统属性获取
     * @param propName 属性名
     * @param defaultValue 默认值, 如果属性值为空时则返回默认值
     * @return
     * @author WillYang
     * @since 2015年7月23日上午9:16:57
     */
    public static String getProp(Properties properties, String propName, String defaultValue)
    {
        String value = null;
        if (properties != null)
        {
            value = properties.getProperty(propName);
        }
        else
        {
            value = System.getProperty(propName);
        }
        if (isEmpty(value))
        {
            value = defaultValue;
        }
        else
        {
            value = value.trim();
        }
        return value;
    }

    private static boolean isEmpty(String value)
    {
        return (value == null || "".equals(value.trim()));
    }
    
    /**
     * 
     * 功能描述
     * Properties to map
     * @param relativePath
     * @return
     * @throws Exception
     * @author lh
     * @since 2015年9月18日
     */
    @SuppressWarnings("unchecked")
	public static Map<String, String> properties2map(String relativePath)
    {
    	Properties properties = new Properties();
		try {
			properties = getProperties(relativePath, null);
		} catch (Exception e) {
		}
    	@SuppressWarnings("rawtypes")
		Map<String, String> map = new HashMap<String, String>((Map) properties);
        return map;
    }
}
