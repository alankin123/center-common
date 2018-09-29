package com.alankin.center.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 项目读取多配置文件工具类
 * 
 * @author zhiping_chen
 *   这个对象在整个项目只创建一次，而且它在项目运行过程中，对象状态不会发生变化,其他所有的线程都以只读的方式进行访问，所以是线程安全的
 */
public class PropertiesUtil
{
    public static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    private ClassLoader loader;
    private static final Map<String, Properties> propertyMap = new HashMap<String, Properties>();

    public PropertiesUtil()
    {
    	this.loader = Thread.currentThread().getContextClassLoader();
    }
 
    /**
     * 根据项目中文件名称获取properties属性
     * 
     * @param fileName
     * @return
     * @throws IOException
     * @throws KTMBaseException
     */
    public Properties getPropertyByFileName(final String fileName)
    {
        InputStream in;
        Properties pro = null;
       
        try
		{
			in = loader.getResourceAsStream(fileName + ".properties");
            
            if(in==null)
            {
            	log.error("加载配置文件"+fileName + ".properties"+"失败");
            }
            else
            {
		        pro = new Properties();
		        
		        pro.load(in);
		        
		        in.close();
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

        return pro;
    }

    /**
     * 根据项目中文件名称获取properties属性
     * 
     * @param fileName
     * @return
     * @throws IOException
     * @throws KTMBaseException
     */
    public static String getStringValue(final String fileName,final String key, String defaultValue)
    {
        try
        {
            String value = propertyMap.get(fileName).getProperty(key);
            
            if(value==null)
            	return defaultValue;
            
        	return value;
        }
        catch(Exception e)
        {
        	return defaultValue;
        }
    }
    
    public int getIntValue(final String fileName,final String key, int defaultValue)
    {      
        try
        {
            String value = propertyMap.get(fileName).getProperty(key);
            
            if(value==null)
            	return defaultValue;
            
        	return Integer.parseInt(value);
        }
        catch(Exception e)
        {
        	return defaultValue;
        }
    }
    
    public boolean getBooleanValue(final String fileName,final String key, boolean defaultValue)
    {
        try
        {
            String value = propertyMap.get(fileName).getProperty(key);
            
            if(value==null)
            	return defaultValue;
            
        	return Boolean.parseBoolean(value);
        }
        catch(Exception e)
        {
        	return defaultValue;
        }
    }
    
    /**
     * 加载项目中指定的配置文件,并用Map保存
     * 
     * @param filepaths
     * @throws KTMBaseException
     * @throws IOException
     */
    public void loadProperties(final String... fileNames) throws Exception 
    {
        try
        {
            InputStream in;
            Properties pro;
            
            for (final String fileName : fileNames)
            {
                in = loader.getResourceAsStream(fileName + ".properties");
                
                if(in==null)
                {
                	throw new Exception("加载配置文件"+fileName + ".properties"+"失败");
                }
                else
                {
	                pro = new Properties();
	                pro.load(in);
	                propertyMap.put(fileName, pro);
	                log.info("Load " + fileName + ".properties successfully");
	                in.close();
                }
            }
        }
        catch (Exception e)
        {
        	log.error("加载配置文件失败",e);
        	throw e;
        }
    }
}
