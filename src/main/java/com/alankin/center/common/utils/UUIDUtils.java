package com.alankin.center.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import com.alankin.center.common.global.MapCache;

public class UUIDUtils {

	/**
	 * UUID
	 */
	public static String getId() {
		return UUID.randomUUID().toString().toLowerCase();
	}

	/**
	 * UUID
	 */
	public static void putIdByEntity(Object entity) {
		Field[] fields = entity.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			// 判断字段是否为序列版本号
			if ("serialVersionUID".equals(fieldName)) {
				continue;
			}
			Class<?> fieldType = fields[i].getType();
			// 判断字段是否为字符串
			if (String.class != fieldType) {
				break;
			}

			try {
				Method setMethod = MapCache.getMethodByType(entity.getClass(), "set" + StringUtil.firstCharUpperCase(fieldName), fieldType);
				Method getMethod = MapCache.getMethodByType(entity.getClass(), "get" + StringUtil.firstCharUpperCase(fieldName));
				// 判断主键是否为空
				if (!StringUtils.isEmpty(getMethod.invoke(entity) + "")) {
					break;
				}
				// 为主键添加UUID
				setMethod.invoke(entity, getId());
				break;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				break;
			}
		}
	}
}
