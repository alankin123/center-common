package com.alankin.center.common.json.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alankin.center.common.json.IJsonConvertor;
import com.alankin.center.common.json.INameFilter;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * <p>封装alibaba FastJson转换
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: Shenzhen XGD SoftWare
 * </p>
 * @since 2015年7月22日
 * @version 1.0
 * @author WillYang
 */
public class FastJsonConvertor implements IJsonConvertor {

    public String convert(Object value) {
        return JSON.toJSONString(value, SerializerFeature.DisableCircularReferenceDetect);
    }

    public <T> T parse(String value, Class<T> clazz) {
        return JSON.parseObject(value, clazz);
    }

    @Override
    public String convert(Object value, INameFilter filter) {
        return JSON.toJSONString(value, new FastJsonNameFilter(filter));
    }

    private class FastJsonNameFilter implements NameFilter {

        private INameFilter nameFilter;

        public FastJsonNameFilter(INameFilter nameFilter) {
            this.nameFilter = nameFilter;
        }

        @Override
        public String process(Object object, String name, Object value) {
            return nameFilter.process(object, name, value);
        }

    }
}
