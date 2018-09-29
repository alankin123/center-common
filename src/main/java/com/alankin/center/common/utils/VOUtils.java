package com.alankin.center.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alankin.center.common.global.MapCache;

public class VOUtils {

	private static Logger logger = LoggerFactory.getLogger(VOUtils.class);

	/**
	 * MAP转换为实体对象
	 * 
	 * @param supperClass
	 *            父级实体类型
	 * @param childClass
	 *            子级实体类型
	 * @param map
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Class supperClass, Class childClass, Map<String, Object> map)
	        throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException {

		Object supperObj = null;
		try {
			supperObj = supperClass.newInstance();
		} catch (Exception e) {
			if (map == null || map.isEmpty())
				return supperObj;
			return map.get(map.keySet().iterator().next());
		}
		if (map == null || map.isEmpty())
			return supperObj;

		setFieldValue(supperObj, map, childClass);

		return supperObj;
	}

	/**
	 * MAP转换为实体对象
	 * 
	 * @param supperClass
	 *            父级实体类型
	 * @param childClass
	 *            子级实体类型
	 * @param map
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Class classType, Map<String, Object> map)
	        throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException {

		Object supperObj = null;
		try {
			supperObj = classType.newInstance();
		} catch (Exception e) {
			return null;
		}
		if (map == null || map.isEmpty())
			return supperObj;

		setFieldValue(supperObj, map);

		return supperObj;
	}

	/**
	 * 往对象属性里赋值
	 * 
	 * @param field
	 * @param obj
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setFieldValue(Field field, Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		int mod = field.getModifiers();
		if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
			return;
		}
		field.setAccessible(true);

		value = convertValue(value, field.getType(), field.getName());

		if (value != null) {
			field.set(obj, value);
		}

	}

	/**
	 * 调用set方法往对象属性里赋值
	 * 
	 * @param field
	 * @param obj
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void setFieldValue(Object obj, Map<String, Object> map, Class<?> childClass)
	        throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		List<Method> methods = MapCache.getAllSeters(obj.getClass());
		for (Method m : methods) {
			Object value = null;
			Class<?> type = m.getParameterTypes()[0];
			String paramName = StringUtil.firstCharLowCase(m.getName().substring(3));
			if (type.isAssignableFrom(childClass)) {
				Object childObj = childClass.newInstance();
				setFieldValue(childObj, map);
				value = childObj;
			} else {
				value = map.get(paramName);
				if (value != null) {
					value = convertValue(value, m.getParameterTypes()[0], paramName);
				}
			}
			if (value != null) {
				try {
					m.invoke(obj, value);
				} catch (Exception e) {
					logger.error("SET字段属性失败！{}", paramName, e);
				}
			}
		}

	}

	/**
	 * 调用set方法往对象属性里赋值
	 * 
	 * @param field
	 * @param obj
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setFieldValue(Object obj, Map<String, Object> map) throws IllegalArgumentException, IllegalAccessException {
		List<Method> methods = MapCache.getAllSeters(obj.getClass());
		for (Method m : methods) {
			String paramName = StringUtil.firstCharLowCase(m.getName().substring(3));
			Object value = map.get(paramName);
			if (value == null) {
				continue;
			}
			value = convertValue(value, m.getParameterTypes()[0], paramName);
			if (value != null) {
				try {
					m.invoke(obj, value);
				} catch (Exception e) {
					logger.debug("SET字段属性失败！{}", paramName);
				}
			}
		}

	}

	/**
	 * 设置子节点的对象属性
	 * 
	 * @param supperObj
	 * @param childField
	 * @param childClass
	 * @param map
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void setChildFiled(Object supperObj, Field childField, Class<?> childClass, Map<String, Object> map)
	        throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Object childObj = null;
		Field[] fields = getAllFields(childClass);
		for (Field field : fields) {
			String fieldName = field.getName();
			Object fieldValue = map.get(fieldName);
			if (fieldValue != null) {
				if (childObj == null) {
					childObj = childClass.newInstance();
					setFieldValue(childField, supperObj, childObj);
				}
				setFieldValue(field, childObj, fieldValue);
			}
		}

	}

	public static Field[] getAllFields(Class<?> classType) {
		// 本类字段
		Field[] thisFields = classType.getDeclaredFields();
		// 父类公开字段
		Field[] supperfields = classType.getFields();
		// 合并
		Field[] fields = new Field[thisFields.length + supperfields.length];
		System.arraycopy(thisFields, 0, fields, 0, thisFields.length);
		System.arraycopy(supperfields, 0, fields, thisFields.length, supperfields.length);

		return fields;
	}

	/**
	 * 将一个 JavaBean 对象转化为一个 Map
	 * 
	 * @param bean
	 *            要转化的JavaBean 对象
	 * @return 转化出来的 Map 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map convertBean(Object bean) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		Class type = bean.getClass();
		Map returnMap = new HashMap();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}

	/**
	 * 数组参数转换赋值
	 * 
	 * @param value
	 * @param classType
	 * @return
	 */
	private static Object convertValueByArgs(Object value, Class<?> classType, String paramName) {
		// 如果值本身就是一样的数组类型，则直接赋值
		if (classType.isInstance(value)) {
			return value;
		}

		// 将入参都先转为字符串类型，支持逗号分隔形式
		String[] strArgs = null;
		if (value.getClass().isArray()) {
			strArgs = (String[]) value;
		} else {
			strArgs = (value + "").split(",");
		}
		// 转换成数组之后再判断一次
		if (classType.isInstance(value)) {
			return value;
		}

		// 根据不用类型，转换为指定类型的数组
		// 字符串转长整型数组
		if (classType == long[].class) {
			long[] longArgs = new long[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					longArgs[i] = Long.parseLong(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return longArgs;
		}
		if (classType == Long[].class) {
			Long[] longArgs = new Long[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					longArgs[i] = Long.parseLong(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return longArgs;
		}

		// 字符串转整型数组
		if (classType == int[].class) {
			int[] intArgs = new int[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					intArgs[i] = Integer.parseInt(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return intArgs;
		}
		if (classType == Integer[].class) {
			Integer[] intArgs = new Integer[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					intArgs[i] = Integer.parseInt(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return intArgs;
		}

		// 字符串转实数数组
		if (classType == double[].class) {
			double[] doubleArgs = new double[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					doubleArgs[i] = Double.parseDouble(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return doubleArgs;
		}
		if (classType == Double[].class) {
			Double[] doubleArgs = new Double[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					doubleArgs[i] = Double.parseDouble(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return doubleArgs;
		}

		// 字符串转实数数组
		if (classType == float[].class) {
			float[] floatArgs = new float[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					floatArgs[i] = Float.parseFloat(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return floatArgs;
		}
		if (classType == Float[].class) {
			Float[] floatArgs = new Float[strArgs.length];
			for (int i = 0; i < strArgs.length; i++) {
				try {
					floatArgs[i] = Float.parseFloat(strArgs[i] + "");
				} catch (Exception e) {
					printConvertTypeError(paramName, classType.getName(), strArgs[i] + "");
					continue;
				}
			}
			return floatArgs;
		}

		return strArgs;

	}

	/**
	 * 参数转换赋值
	 * 
	 * @param value
	 * @param classType
	 * @return
	 */
	public static Object convertValue(Object value, Class<?> classType, String paramName) {
		if (classType.isInstance(value)) {
			return value;
		}

		if (StringUtils.isEmpty(value)) {
			return initNumberValue(classType);
		}

		// 数组处理
		if (classType.isArray()) {
			return convertValueByArgs(value, classType, paramName);
		} else if (value.getClass().isArray()) {
			// 取数组的第一个
			String[] strArgs = (String[]) value;
			if (strArgs.length > 0) {
				value = strArgs[0];
				// 转换后类型相同直接返回
				if (classType.isInstance(value)) {
					return value;
				}
				// 转换后是否为空再验证一次
				if (StringUtils.isEmpty(value)) {
					return initNumberValue(classType);
				}
			} else {
				return null;
			}
		}

		return convertValueByClassType(value, classType, paramName);
	}

	/**
	 * 根据参数类型转换参数
	 * 
	 * @param value
	 * @param classType
	 * @param paramName
	 * @return
	 */
	public static Object convertValueByClassType(Object value, Class<?> classType, String paramName) {
		// 长整型
		if (classType == long.class || classType == Long.class) {
			try {
				return Long.parseLong(value.toString());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// 整型
		if (classType == int.class || classType == Integer.class) {
			try {
				return Integer.parseInt(value.toString());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// 实数
		if (classType == double.class || classType == Double.class) {
			try {
				return Double.parseDouble(value.toString());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// 实数
		if (classType == float.class || classType == Float.class) {
			try {
				return Float.parseFloat(value.toString());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// 金额
		if (classType == BigDecimal.class) {
			try {
				return new BigDecimal(value.toString());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// 布尔
		if (classType == boolean.class || classType == Boolean.class) {
			return "1".equals(value) || "true".equals(value) ? true : false;
		}

		// 日期时间
		if (classType == Date.class) {
			try {
				return DateUtilSafe.parseDateAll(value.toString());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// 日期
		if (classType == LocalDate.class) {
			try {
				return LocalDateTime.ofInstant(DateUtilSafe.parseDateAll(value.toString()).toInstant(), ZoneId.systemDefault()).toLocalDate();
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// 时间
		if (classType == LocalTime.class) {
			try {
				return DateUtilSafe.parseLocalTime(value.toString());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		// java8的日期时间
		if (classType == LocalDateTime.class) {
			try {
				return LocalDateTime.ofInstant(DateUtilSafe.parseDateAll(value.toString()).toInstant(), ZoneId.systemDefault());
			} catch (Exception e) {
				printConvertTypeError(paramName, classType.getName(), value + "");
				return null;
			}
		}

		return value;
	}

	/**
	 * 参数转换赋值
	 * 
	 * @param value
	 * @param classType
	 * @return
	 */
	public static Object convertJsonValueByClassType(Object value, Parameter parameter, String paramName) {
		// JSON数组
		if (List.class == parameter.getType() && String.class.isInstance(value)) {
			Class<?> entityClass = getActualType(parameter);
			if (entityClass != null) {
				try {
					return JSONArray.parseArray(value + "", entityClass);
				} catch (Exception e) {
					logger.error("json转换错误！", e);
					return null;
				}
			}
		}

		// 普通JSON对象
		try {
			return JsonUtils.parse(value + "", parameter.getClass());
		} catch (Exception e) {
			logger.error("json转换错误！", e);
			return null;
		}

	}

	/**
	 * 参数转换赋值
	 * 
	 * @param value
	 * @param classType
	 * @return
	 */
	public static Object convertValue(Object value, Parameter parameter, String annotationName) {
		// List类型用JSON数组解析
		if (List.class == parameter.getType() && String.class.isInstance(value)) {
			return convertJsonValueByClassType(value, parameter, annotationName);
		}

		return convertValue(value, parameter.getType(), annotationName);
	}

	/**
	 * 获取方法参数的泛型(默认只取第一个泛型)
	 * 
	 * @param parameter
	 * @return
	 */
	public static Class<?> getActualType(Parameter parameter) {
		Type type = parameter.getParameterizedType();
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			// 获取参数的泛型列表
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments.length == 1) {
				return (Class<?>) actualTypeArguments[0];
			}
		}
		return null;
	}

	/**
	 * 参数类型转换错误日志打印
	 * 
	 * @param paramName
	 * @param classType
	 * @param value
	 */
	private static void printConvertTypeError(String paramName, String classType, String value) {
		logger.error("转换参数时出错。参数名：{}，参数类型:{},要转换的值:{}.", paramName, classType, value);
	}

	/**
	 * 参数值为NULL时，返回基本类型初始值，以免赋值报错
	 * 
	 * @param classType
	 * @return
	 */
	private static Object initNumberValue(Class<?> classType) {
		if (classType == int.class) {
			return 0;
		} else if (classType == long.class) {
			return 0l;
		} else if (classType == double.class) {
			return 0d;
		} else if (classType == float.class) {
			return 0f;
		}

		return null;
	}

	/**
	 * 判断一个类是否为基本类型
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isWrapClass(Class<?> clz) {
		try {
			return clz.isPrimitive() || ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

}
