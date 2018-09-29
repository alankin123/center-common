package com.alankin.center.common.json;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * <p> 通用Json转换接口
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: Shenzhen XGD SoftWare
 * </p>
 * @since 2015年7月22日
 * @version 1.0
 * @author WillYang
 */
public interface IJsonConvertor {

    String convert(Object value);

    String convert(Object value, INameFilter filter);

    <T> T parse(String value, Class<T> clazz);
}
