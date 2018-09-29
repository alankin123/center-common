package com.alankin.center.common.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alankin.center.common.api.IEncryptService;
import com.alankin.center.common.enctrypt.EncryptUtils;
import com.alankin.center.common.utils.DateUtilSafe;
import com.alankin.center.common.utils.StringUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EncryptRouteService extends ApplicationObjectSupport implements IEncryptService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String SERVICE_AFTER = "Service";

	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.setDateFormat(DateUtilSafe.getSdf(DateUtilSafe.simple));
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	@Override
	public String encryptProcess(String mode, String serviceName, String methodName, Map reqMap) {
		logger.info("=========加密接口调用请求开始：======" + serviceName + "====" + methodName + "=========" + reqMap);
		// service名字，首字母小写
		StringUtil.firstCharLowCase(serviceName);
		Object service = null;
		try {
			service = getApplicationContext().getBean(serviceName + SERVICE_AFTER);
		} catch (Exception e) {
			logger.error("没有找到对应的模块。", e);
			return "没有找到对应的模块[" + mode + "/" + serviceName + "]";
		}

		if (!(service instanceof EncryptSupperService)) {
			logger.error("该模块[" + serviceName + "]为非加密接口，需要进行登录后访问");
			return "该模块[" + serviceName + "]为非加密接口，需要进行登录后访问";
		}

		if (getReqData(reqMap) == null) {
			return "解密报文出错，请检查报文是否错误";
		}

		Method[] methods = service.getClass().getMethods();
		Object respObj = null;

		boolean runFlag = false;
		for (Method method : methods) {
			if (methodName.equals(method.getName())) {
				runFlag = true;
				try {
					// 调用具体service方法
					respObj = method.invoke(service, getMethodParameter(mode, service.getClass().getPackage().getName(), serviceName, methodName, reqMap));

					break;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | SecurityException
				        | InstantiationException | IOException e) {
					logger.error("路由调用srevice时报错。", e);
				}
			}
		}

		if (!runFlag && respObj == null) {
			logger.error("该模块中没有找到方法:{}。", methodName);
			return "该模块中没有找到方法:" + serviceName + "." + methodName + "。";
		}
		String result = jsonFormatResp(respObj, reqMap);
		logger.info("=========调用请求结束：======{}", result);
		return result;
	}

	/**
	 * 根据名字获取service类
	 * 
	 * @param mode
	 * @param servicePackage
	 * @param serviceName
	 * @return
	 */
	private Class<?> getServiceByName(String servicePackage, String serviceName) {
		StringBuffer sb = new StringBuffer(servicePackage);
		sb.append(".").append(serviceName.substring(0, 1).toUpperCase()).append(serviceName.substring(1)).append(SERVICE_AFTER);
		try {
			return Class.forName(sb.toString());
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 将返回对象转换为json，存到InterfaceRespVO中
	 * 
	 * @param resp
	 * @return
	 */
	private String jsonFormatResp(Object resp, Map reqMap) {
		String resultStr = null;
		if (resp == null) {
			logger.error("service调用返回值为空");
			return "模块调用返回值为空";
		}
		try {
			// 转换为json
			resultStr = objectMapper.writeValueAsString(resp);
			// 加密报文
			resultStr = EncryptUtils.encodeData(reqMap.get("merchantNo") + "", resultStr);

			return resultStr;
		} catch (JsonProcessingException e) {
			logger.error("返回对象转换为josn时报错。", e);
			return "返回对象转换为josn时报错。";
		}

	}

	/**
	 * 从MAP中提取参数封装成方法入参
	 * 
	 * @param mode
	 * @param servicePackage
	 * @param serviceName
	 * @param method
	 * @param reqMap
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private Object[] getMethodParameter(String mode, String servicePackage, String serviceName, String methodName, Map<String, Object> reqMap)
	        throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException, JsonParseException,
	        JsonMappingException, IOException {
		Parameter[] parameters = null;

		// 获取service 方法参数
		Method[] methods = getServiceByName(servicePackage, serviceName).getMethods();
		for (Method method : methods) {
			if (methodName.equals(method.getName())) {
				parameters = method.getParameters();
				break;
			}
		}

		if (parameters == null || parameters.length == 0) {
			return new Object[0];
		}

		Object[] values = new Object[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];

			String data = reqMap.get("data") + "";
			String merchantNo = reqMap.get("merchantNo") + "";

			values[i] = objectMapper.readValue(data, parameter.getType());
			try {
				Method method = parameter.getType().getMethod("setMerchantNo", String.class);
				method.invoke(values[i], merchantNo);
			} catch (NoSuchMethodException | InvocationTargetException e) {
			}

		}
		return values;
	}

	private String getReqData(Map<String, Object> reqMap) {
		if (!reqMap.containsKey("data") || !reqMap.containsKey("merchantNo")) {
			logger.error("调用缺少参数,merchantNo,data");
			return null;
		}
		// 解密
		String data = reqMap.get("data") + "";
		String merchantNo = reqMap.get("merchantNo") + "";

		try {
			data = EncryptUtils.decodeData(merchantNo, data);
		} catch (Exception e) {
			logger.error("解密报文出错", e);
			return null;
		}

		reqMap.put("data", data);

		return data;
	}

}
