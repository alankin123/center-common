package com.alankin.center.common.global;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alankin.center.common.utils.AopTargetUtils;

public class MapCache {

	public static Map<Class<?>, List<Method>> SET_GET_METHOD_LIST_CACHE = new ConcurrentHashMap<>();
	public static Map<Class<?>, Method[]> METHOD_ARGS_CACHE = new ConcurrentHashMap<>();
	public static Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
	public static Map<String, Class<?>> CLASSFORNAME_CACHE = new ConcurrentHashMap<>();

	public static List<Method> getAllSeters(Class<?> classType) {
		// 先从缓存里找
		List<Method> methodList = SET_GET_METHOD_LIST_CACHE.get(classType);
		if (methodList != null) {
			return methodList;
		}

		Method[] methods = getMethods(classType);
		if (methods == null || methods.length == 0) {
			return null;
		}
		methodList = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.getName().startsWith("set") && m.getParameters().length == 1) {
				methodList.add(m);
			}
		}
		SET_GET_METHOD_LIST_CACHE.put(classType, methodList);

		return methodList;
	}

	public static Method[] getMethods(Class<?> classType) {
		// 先从缓存里找
		Method[] methods = METHOD_ARGS_CACHE.get(classType);
		if (methods != null) {
			return methods;
		}

		methods = classType.getMethods();
		METHOD_ARGS_CACHE.put(classType, methods);
		return methods;
	}

	public static Method getMethod(Object proxy, String methodName) {
		return getMethod(AopTargetUtils.getTarget(proxy).getClass(), methodName);
	}

	public static Method getMethod(Class<?> classType, String methodName) {
		String key = classType.getName() + "-" + methodName;
		// 先从缓存里找
		Method method = METHOD_CACHE.get(key);
		if (method != null) {
			return method;
		}

		Method[] methods = getMethods(classType);
		for (Method m : methods) {
			if (methodName.equals(m.getName())) {
				METHOD_CACHE.put(key, m);
				return m;
			}
		}

		return null;
	}

	public static Method getMethodByType(Class<?> classType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		String key = classType.getName() + "-" + methodName;
		// 先从缓存里找
		Method method = METHOD_CACHE.get(key);
		if (method != null) {
			return method;
		}

		method = classType.getMethod(methodName, parameterTypes);
		METHOD_CACHE.put(key, method);
		return method;
	}

	public static Class<?> classForNameByCache(String className) {
		// 先从缓存里找
		Class<?> entityClass = CLASSFORNAME_CACHE.get(className);
		if (entityClass != null) {
			return entityClass;
		}

		try {
			entityClass = Class.forName(className);
			CLASSFORNAME_CACHE.put(className, entityClass);
			return entityClass;
		} catch (Throwable e) {
			return null;
		}
	}

}
