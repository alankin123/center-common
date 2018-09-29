package com.alankin.center.common.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * <p>资源文件获取工具类
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: Shenzhen XGD SoftWare
 * </p>
 * @since 2015年7月22日
 * @version 1.0
 * @author WillYang
 */
public class ResourcesUtils
{
    private ResourcesUtils()
    {
    }
    
    /**
     * 
     * 功能描述
     * 获取资源文件,如果类路径下加载不到则从文件系统加载,资源不存在返回NULL
     * @param filePath
     * @return 资源URL
     * @author WillYang
     * @since 2015年7月22日下午5:29:12
     */
    public static URL getResources(String filePath) {
        URL result = null;
        result = getRelativeResources(filePath);
        if (result == null) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                try
                {
                    result = file.toURI().toURL();
                }
                catch (MalformedURLException e)
                {   
                }
            }
        }
        return result;
    }
    
    /**
     * 
     * 功能描述
     * 获取相对路径的资源文件,加载不到返回NULL
     * @param relativePath
     * @return 资源URL
     * @author WillYang
     * @since 2015年7月22日下午5:23:12
     */
    public static URL getRelativeResources(String relativePath) {
        return Thread.currentThread().getContextClassLoader().getResource(relativePath);
    }
}
