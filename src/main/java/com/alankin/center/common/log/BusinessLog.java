package com.alankin.center.common.log;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alankin.center.common.entity.BaseEntity;
import com.alankin.center.common.entity.SessionUser;
import com.alankin.center.common.service.ThreadContext;
import com.alankin.center.common.utils.DateUtilSafe;
import com.alankin.center.common.utils.StringUtil;
import com.alankin.center.common.vo.BaseRespVO;
import com.alankin.center.common.vo.ValueRespVO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class BusinessLog extends ApplicationObjectSupport {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static long LOG_TYPE_CREATE = 1;
	public static long LOG_TYPE_UPDATE = 2;
	public static long LOG_TYPE_DELETE = 3;

	private static ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setDateFormat(DateUtilSafe.getSdf(DateUtilSafe.simple));
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	@Autowired
	BusinessLogHandler businessLogHandler;

	/**
	 * 方法调用前记录
	 * 
	 * @param serviceName
	 * @param methodName
	 * @param reqMap
	 */
	public void invokBefore(String serviceName, String methodName, Map reqMap) {
		// LogServiceConfig logServiceConfig = businessLogHandler.getLogConfigMap().get(serviceName);
		// if (logServiceConfig == null) {
		// return;
		// }
		//
		// LogMethodConfig logMethodConfig = logServiceConfig.getMethodMap().get(methodName);
		// if (logMethodConfig == null || LOG_TYPE_CREATE != logMethodConfig.getLogType()) {
		// return;
		// }
		//
		// LogBusinessEntity entity = businessLogHandler.instantiationLogBusinessEntity(logServiceConfig,
		// logMethodConfig);
		//
		// saveLogEntity(serviceName, methodName, reqMap, entity);

	}

	/**
	 * 方法调用后记录
	 * 
	 * @param serviceName
	 * @param methodName
	 * @param reqMap
	 */
	public void invokAfter(String serviceName, String methodName, Map reqMap, BaseRespVO respVo) {
		try {
			LogServiceConfig logServiceConfig = businessLogHandler.getLogConfigMap().get(serviceName);
			if (logServiceConfig == null) {
				return;
			}

			LogMethodConfig logMethodConfig = logServiceConfig.getMethodMap().get(methodName);
			if (logMethodConfig == null) {
				return;
			}

			LogBusinessEntity entity = businessLogHandler.instantiationSysLogEntity(logServiceConfig, logMethodConfig);

			saveLogEntity(serviceName, methodName, reqMap, entity, respVo, logServiceConfig);
		} catch (Throwable e) {
			logger.debug("记录业务日志出错!", e);
		}

	}

	/**
	 * 保存日志信息
	 * 
	 * @param serviceName
	 * @param methodName
	 * @param user
	 * @param clentIp
	 * @param reqMap
	 * @param entity
	 * @throws JsonProcessingException
	 */
	private void saveLogEntity(String serviceName, String methodName, Map reqMap, LogBusinessEntity entity, BaseRespVO respVo,
	        LogServiceConfig logServiceConfig) throws JsonProcessingException {

		SessionUser user = ThreadContext.getSessionloginUser(false);
		String clentIp = ThreadContext.getRequestClientIp();

		if (user != null) {
			entity.setCreateUserId(user.getUserId());
			entity.setCreateUserName(user.getNickName());
		}
		entity.setClentIp(clentIp);

		try {
			entity.setParameters(objectMapper.writeValueAsString(businessLogHandler.copyReqMap(reqMap)));
		} catch (JsonProcessingException e) {
			entity.setParameters(e.getMessage());
		}
		entity.setCreateDate(new Date());

		String resutStr = null;
		if (respVo == null) {
			resutStr = "未知";
		} else {
			if (respVo.isSuccess()) {
				resutStr = "成功";
			} else {
				resutStr = "失败，" + objectMapper.writeValueAsString(respVo);
			}
		}
		entity.setOperResut(resutStr);

		Object id = reqMap.get(logServiceConfig.getIdFieldName());
		if (id != null) {
			entity.setRecordId(id + "");
		} else {
			try {
				if (ValueRespVO.class.isInstance(respVo)) {
					ValueRespVO valueRespVO = (ValueRespVO) respVo;
					if (BaseEntity.class.isInstance(valueRespVO.getAaData())) {
						BaseEntity respEntity = (BaseEntity) valueRespVO.getAaData();
						Method method = respEntity.getClass().getMethod("get" + StringUtil.firstCharUpperCase(logServiceConfig.getIdFieldName()));
						id = method.invoke(respEntity);
						entity.setRecordId(id + "");
					}
				}
			} catch (Exception e) {
			}
		}

		saveLog(entity);
	}

	/**
	 * 记录业务日志
	 * 
	 * @param serviceName
	 * @param methodName
	 * @param reqMap
	 */
	private void saveLog(LogBusinessEntity entity) {
		Object bean = null;
		try {
			bean = getApplicationContext().getBean("centerBusinessLog");
		} catch (Exception e) {
			return;
		}
		if (bean == null) {
			return;
		}
		try {
			Method method = bean.getClass().getMethod("saveBusinessLog", LogBusinessEntity.class);
			method.invoke(bean, entity);
		} catch (Throwable e) {
			logger.debug("记录业务日志出错！", e);
		}

	}

}
