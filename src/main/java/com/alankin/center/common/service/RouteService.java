package com.alankin.center.common.service;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.alankin.center.common.Constants;
import com.alankin.center.common.api.IRouteService;
import com.alankin.center.common.entity.BaseEntity;
import com.alankin.center.common.entity.SessionUser;
import com.alankin.center.common.exception.CenterException;
import com.alankin.center.common.exception.CenterValidateException;
import com.alankin.center.common.global.MapCache;
import com.alankin.center.common.utils.AopTargetUtils;
import com.alankin.center.common.utils.DateUtilSafe;
import com.alankin.center.common.utils.StringUtil;
import com.alankin.center.common.utils.VOUtils;
import com.alankin.center.common.vo.BaseRespVO;
import com.alankin.center.common.vo.BaseVO;
import com.alankin.center.common.vo.InterfaceRespVO;
import com.alankin.center.common.vo.Param;
import com.alankin.center.common.vo.ParamJson;
import com.alankin.center.common.vo.ValueRespVO;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class RouteService extends ApplicationObjectSupport implements IRouteService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String SERVICE_AFTER = "Service";

	private static final String ENTITY_AFTER = "Entity";

	// 超过该值的请求处理，将被认为是慢的处理
	private static final long SLOW_LIMIT = 10000l;

	public static ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.setDateFormat(DateUtilSafe.getSdf(DateUtilSafe.simple));
		// objectMapper.setSerializationInclusion(Include.NON_NULL);
		// objectMapper.setSerializationInclusion(Include.NON_EMPTY);

		DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder().appendValue(HOUR_OF_DAY, 2).appendLiteral(':').appendValue(MINUTE_OF_HOUR, 2)
		        .optionalStart().appendLiteral(':').appendValue(SECOND_OF_MINUTE, 2).toFormatter();
		SimpleModule module = new SimpleModule();
		module.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
		module.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
		module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
		objectMapper.registerModule(module);
	}

	@Override
	public BaseRespVO webProcess(String mode, String serviceName, String methodName, Map reqMap) {
		logger.debug("=========调用请求开始：======" + serviceName + "====" + methodName + "=========" + reqMap);
		if (reqMap == null) {
			logger.error("解析请求参数【reqMap】失败，请检查依赖实体的版本是否一致！");
			return new InterfaceRespVO(9, "解析请求参数【reqMap】失败，请检查依赖实体的版本是否一致！");
		}
		long startTime = System.currentTimeMillis();
		// service名字，首字母小写
		serviceName = StringUtil.firstCharLowCase(serviceName);
		Object service = null;
		try {
			service = getApplicationContext().getBean(serviceName + SERVICE_AFTER);
		} catch (Exception e) {
			logger.error("没有找到对应的模块[" + mode + "/" + serviceName + "]", e);
			return new InterfaceRespVO(9, "没有找到对应的模块[" + mode + "/" + serviceName + "]");
		}

		BaseRespVO resp = null;
		// 验证该service是否有自定义限制
		if (BaseService.class.isInstance(service)) {
			BaseService bs = (BaseService) service;
			resp = bs.isPass(mode, serviceName, methodName, reqMap);
			if (resp != null) {
				logger.debug("service验证不通过.{}", resp);
				return jsonFormatResp(resp);
			}
		}

		Method method = MapCache.getMethod(service, methodName);
		if (method == null) {
			logger.error("该模块中没有找到方法:{}。", methodName);
			return new InterfaceRespVO(9, "该模块中没有找到方法:" + serviceName + "." + methodName + "。");
		}

		Object[] parameters = null;

		try {
			// 提取上下文信息
			getReqContext(reqMap);
			// 初始化请求参数
			initReqMap(reqMap, serviceName, methodName);
			// 根据方法参数类型，组装入参
			parameters = getMethodParameter(service, method, reqMap);

			// 记录业务日志
			logBefore(serviceName, methodName, reqMap);
			// 调用具体service方法
			Object respObj = method.invoke(service, parameters);
			// 组装返回参数
			if (BaseRespVO.class.isInstance(respObj)) {
				resp = (BaseRespVO) respObj;
			} else {
				ValueRespVO valueRespVO = new ValueRespVO(respObj);
				resp = valueRespVO;
			}
			// 记录业务日志
			logAfter(serviceName, methodName, reqMap, resp);
		} catch (Throwable e) {
			Throwable chiledError = e;
			while (chiledError.getCause() != null) {
				chiledError = chiledError.getCause();
			}
			if (CenterException.class.isInstance(chiledError)) {
				resp = ((CenterException) chiledError).getRespVO();
			} else if (IllegalArgumentException.class.isInstance(chiledError)) {
				resp = InterfaceRespVO.error("参数类型错误：===" + parameters + "=====" + chiledError.getClass().getName() + ":" + chiledError.getMessage());
			} else {
				resp = InterfaceRespVO.error(chiledError.getClass().getName() + ":" + chiledError.getMessage());
			}

			// 校验合法性异常不需要打印日志
			if (!CenterValidateException.class.isInstance(chiledError)) {
				logger.error("路由调用srevice时报错。", e);
			}
		} finally {
			ThreadContext.unbindContext();
		}

		InterfaceRespVO interfaceResp = jsonFormatResp(resp);
		logger.debug("=========调用请求结束：======{}", methodName.indexOf("list") > -1 ? resp.getState() + ":" + resp.getMsg() : resp);
		long time = System.currentTimeMillis() - startTime;
		logger.debug("调用耗时:" + (time > SLOW_LIMIT ? "slow--" : "") + time);
		return interfaceResp;
	}

	/**
	 * 调用方法前，记录业务日志
	 * 
	 * @param serviceName
	 * @param methodName
	 * @param reqMap
	 */
	private void logBefore(String serviceName, String methodName, Map reqMap) {
		saveLog(serviceName, methodName, reqMap, "invokBefore", null);
	}

	/**
	 * 调用方法前，记录业务日志
	 * 
	 * @param serviceName
	 * @param methodName
	 * @param reqMap
	 */
	private void logAfter(String serviceName, String methodName, Map reqMap, BaseRespVO resp) {
		saveLog(serviceName, methodName, reqMap, "invokAfter", resp);
	}

	/**
	 * 记录业务日志
	 * 
	 * @param serviceName
	 * @param methodName
	 * @param reqMap
	 */
	private void saveLog(String serviceName, String methodName, Map reqMap, String logMethodName, BaseRespVO resp) {
		Object bean = null;
		try {
			bean = getApplicationContext().getBean("businessLog");
		} catch (Exception e) {
			return;
		}
		if (bean == null) {
			return;
		}
		try {
			if ("invokBefore".equals(logMethodName)) {
				Method method = MapCache.getMethodByType(bean.getClass(), logMethodName, String.class, String.class, Map.class);
				method.invoke(bean, serviceName, methodName, reqMap);
			} else {
				Method method = MapCache.getMethodByType(bean.getClass(), logMethodName, String.class, String.class, Map.class, BaseRespVO.class);
				method.invoke(bean, serviceName, methodName, reqMap, resp);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.debug("记录业务日志出错！", e);
		}

	}

	/**
	 * 根据名字获取实体类
	 * 
	 * @param mode
	 * @param servicePackage
	 * @param serviceName
	 * @return
	 */
	public static Class<?> getEntityByService(Object service) {
		String entityPath = AopTargetUtils.getTarget(service).getClass().getName();
		entityPath = entityPath.replaceAll(".service.", ".entity.");
		entityPath = entityPath.replaceAll(SERVICE_AFTER, ENTITY_AFTER);

		return MapCache.classForNameByCache(entityPath);
	}

	/**
	 * 将返回对象转换为json，存到InterfaceRespVO中
	 * 
	 * @param resp
	 * @return
	 */
	private InterfaceRespVO jsonFormatResp(BaseRespVO resp) {
		if (resp == null) {
			logger.error("service调用返回值为空");
			return new InterfaceRespVO(9, "模块调用返回值为空");
		}
		InterfaceRespVO returnResp = new InterfaceRespVO(resp);

		if (!resp.isSuccess()) {
			return returnResp;
		}
		try {
			returnResp.setRespData(objectMapper.writeValueAsString(resp));
			return returnResp;
		} catch (JsonProcessingException e) {
			logger.error("返回对象转换为josn时报错。", e);
			return new InterfaceRespVO(9, "返回对象转换为josn时报错。");
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
	 * @throws CenterException
	 */
	private Object[] getMethodParameter(Object service, Method method, Map<String, Object> reqMap)
	        throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException, CenterException {
		Parameter[] parameters = method.getParameters();
		// 获取service 方法参数
		if (parameters == null || parameters.length == 0) {
			return new Object[0];
		}

		Object[] values = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			if (BaseEntity.class.isAssignableFrom(parameter.getType()) || BaseVO.class.isAssignableFrom(parameter.getType())) {
				// 先从泛型里面取子级类
				Class<?> childClass = VOUtils.getActualType(parameter);
				if (childClass == null) {
					// 没有设置泛型就从相同包名的entity中取
					childClass = getEntityByService(service);
				}
				if (childClass == null) {
					values[i] = VOUtils.convertMap(parameter.getType(), reqMap);
				} else {
					values[i] = VOUtils.convertMap(parameter.getType(), childClass, reqMap);
				}
			} else if (Map.class.isAssignableFrom(parameter.getType())) {
				// MAP直接设置
				values[i] = reqMap;
			} else {
				// 根据注解名字对应上的参数赋值
				Param param = parameter.getAnnotation(Param.class);
				if (param != null) {
					values[i] = VOUtils.convertValue(reqMap.get(param.value()), parameter, param.value());
					continue;
				}
				ParamJson paramJson = parameter.getAnnotation(ParamJson.class);
				if (paramJson != null) {
					values[i] = VOUtils.convertJsonValueByClassType(reqMap.get(paramJson.value()), parameter, paramJson.value());
					continue;
				}

			}

		}
		return values;
	}

	/**
	 * 填充默认的调用参数
	 * 
	 * @param reqMap
	 * @param methodName
	 * @throws CenterException
	 */
	public void initReqMap(Map<String, Object> reqMap, String serviceName, String methodName) throws CenterException {
		// 从session中注入数据权限信息
		SessionUser sessionUser = null;
		try {
			sessionUser = ThreadContext.getSessionloginUser();
		} catch (Exception e) {
			return;
		}

		if (methodName.indexOf("update") == -1 && methodName.indexOf("delete") == -1) {
			reqMap.put("sysId", sessionUser.getSysId());
		}
	}

	/**
	 * 从请求参数中提取上下文信息
	 * 
	 * @param reqMap
	 * @param methodName
	 */
	public void getReqContext(Map<String, Object> reqMap) {
		// 从session中注入数据权限信息
		Object obj = reqMap.get(Constants.SESSION_LOGIN_USER);
		if (obj != null) {
			// 存入上下文中
			ThreadContext.setSessionloginUser((SessionUser) obj);
		}

		// 客户端IP
		obj = reqMap.get(Constants.REQUEST_CLIENT_IP);
		if (obj != null) {
			// 存入上下文中
			ThreadContext.setRequestClientIp(obj + "");
		}

	}

}
