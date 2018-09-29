package com.alankin.center.common.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import com.alankin.center.common.Constants;
import com.alankin.center.common.global.MapCache;
import com.alankin.center.common.service.RouteService;
import com.alankin.center.common.threadPool.CenterThreadPool;
import com.alankin.center.common.utils.StringUtils;

@SuppressWarnings({ "rawtypes" })
@Service
public class BusinessLogHandler extends ApplicationObjectSupport {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	static Lock lock = new ReentrantLock();
	static Condition condition = lock.newCondition();
	static boolean initFlag = false;

	private static PropertiesLog propertiesLog = PropertiesLog.getInstans();

	private static Map<String, LogServiceConfig> logConfigMap;

	/**
	 * 初始化
	 */
	@PostConstruct
	public void init() {
		// 异步完成
		CenterThreadPool.asyncProxyExecutor(this, "initConfig");
	}

	/**
	 * 初始化日志配置
	 */
	public void initConfig() {
		logger.info("加载业务日志配置");
		synchronized (this) {
			logConfigMap = new HashMap<String, LogServiceConfig>();

			Map<String, String> service = propertiesLog.getAllProps();
			if (service == null || service.isEmpty()) {
				return;
			}
			for (String key : service.keySet()) {
				if (key.endsWith(".name")) {
					LogServiceConfig logServiceConfig = new LogServiceConfig();
					logServiceConfig.setServiceName(key.replaceAll(".name", ""));
					logServiceConfig.setNickName(service.get(key));
					logServiceConfig.setIdFieldName(getIdFieldName(logServiceConfig.getServiceName()));
					logServiceConfig.setMethodMap(getMethodList(logServiceConfig.getServiceName()));
					logServiceConfig.setEntityFieldNameMap(getEntityFieldNameMap(logServiceConfig.getServiceName()));
					logConfigMap.put(logServiceConfig.getServiceName(), logServiceConfig);
				}
			}

			initFlag = true;
			lock.lock();
			condition.signalAll();
			lock.unlock();
			logger.info("" + logConfigMap);
		}

	}

	public Map<String, LogServiceConfig> getLogConfigMap() {
		if (!initFlag) {
			lock.lock();
			condition.signalAll();
			lock.unlock();
		}

		return logConfigMap;
	}

	/**
	 * 清理请求中的无用参数
	 * 
	 * @param reqMap
	 * @return
	 */
	public Map copyReqMap(Map<String, Object> reqMap) {
		Map copyMap = (Map) ((HashMap) reqMap).clone();
		copyMap.remove("token");
		for (Field field : Constants.class.getDeclaredFields()) {
			copyMap.remove(field.getName());
		}
		return copyMap;
	}

	private String getIdFieldName(String serviceName) {
		String idName = propertiesLog.getValue(serviceName + ".id");
		if (StringUtils.isEmpty(idName)) {
			idName = "id";
		}
		return idName;
	}

	/**
	 * 根据serviceName 获取entity字段中文名
	 * 
	 * @param serviceName
	 * @return
	 */
	private Map<String, String> getEntityFieldNameMap(String serviceName) {
		Object service = null;
		try {
			service = getApplicationContext().getBean(serviceName + "Service");
		} catch (Exception e) {
			return null;
		}
		try {
			Class entity = RouteService.getEntityByService(service);
			Map<String, String> entityNameMap = new HashMap<String, String>();
			for (Field field : entity.getDeclaredFields()) {
				if ("serialVersionUID".equals(field.getName())) {
					continue;
				}
				String entityFieldName = null;
				com.alankin.center.common.vo.field param = field.getAnnotation(com.alankin.center.common.vo.field.class);
				if (param == null) {
					entityFieldName = field.getName();
				} else {
					entityFieldName = param.value();
				}
				entityNameMap.put(field.getName(), entityFieldName);
			}

			return entityNameMap;
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 初始化方法配置
	 * 
	 * @param serviceName
	 * @return
	 */
	private Map<String, LogMethodConfig> getMethodList(String serviceName) {
		Object service = null;
		try {
			service = getApplicationContext().getBean(serviceName + "Service");
		} catch (Exception e) {
			return null;
		}
		Map<String, LogMethodConfig> logMethodConfigMap = getLogMethodConfigMapByMethodName(serviceName);

		Map<String, LogMethodConfig> returnMethodMap = new HashMap<String, LogMethodConfig>();
		for (Method method : MapCache.getMethods(service.getClass())) {
			String methodNameLower = method.getName().toLowerCase();
			// 没有配置，则默认取方法名包含 create、update、delete 的方法
			if ((logMethodConfigMap == null || !logMethodConfigMap.containsKey(method.getName()))
			        && (methodNameLower.indexOf("create") > -1 || methodNameLower.indexOf("update") > -1 || methodNameLower.indexOf("delete") > -1)) {
				LogMethodConfig logMethodConfig = new LogMethodConfig();
				logMethodConfig.setLogType(getLogTypeByMethodName(methodNameLower));
				logMethodConfig.setMethodName(method.getName());
				logMethodConfig.setNickName(getMethodNickName(serviceName, logMethodConfig.getMethodName()));
				returnMethodMap.put(logMethodConfig.getMethodName(), logMethodConfig);
			} else if (logMethodConfigMap != null && logMethodConfigMap.containsKey(method.getName())) {
				returnMethodMap.put(method.getName(), logMethodConfigMap.get(method.getName()));
			}
		}

		return returnMethodMap;
	}

	/**
	 * 
	 * @param methodsConfig
	 * @return
	 */
	private Map<String, LogMethodConfig> getLogMethodConfigMapByMethodName(String serviceName) {
		String methodsConfig = propertiesLog.getValue(serviceName + ".method");
		if (StringUtils.isEmpty(methodsConfig)) {
			return null;
		}

		Map<String, LogMethodConfig> logMethodConfigMap = new HashMap<String, LogMethodConfig>();
		String[] methods = methodsConfig.split(",");
		for (int i = 0; i < methods.length; i++) {
			String[] configs = methods[i].split("-");
			if (configs.length < 3) {
				logger.error("日志配置错误，service:[{}],method:[{}]", serviceName, methods[i]);
				continue;
			}
			LogMethodConfig logMethodConfig = new LogMethodConfig();
			logMethodConfig.setMethodName(configs[0]);
			logMethodConfig.setLogType(getLogTypeByMethodName(configs[1]));
			logMethodConfig.setNickName(configs[2]);
			logMethodConfigMap.put(logMethodConfig.getMethodName(), logMethodConfig);
		}

		return logMethodConfigMap;
	}

	/**
	 * 根据方法名字获取日志类型
	 * 
	 * @param methodName
	 * @return
	 */
	private long getLogTypeByMethodName(String methodName) {
		methodName = methodName.toLowerCase();
		if (methodName.indexOf("create") > -1) {
			return BusinessLog.LOG_TYPE_CREATE;
		}
		if (methodName.indexOf("update") > -1) {
			return BusinessLog.LOG_TYPE_UPDATE;
		}
		if (methodName.indexOf("delete") > -1) {
			return BusinessLog.LOG_TYPE_DELETE;
		}

		return 0;
	}

	/**
	 * 根据方法名字获取方法中文名
	 * 
	 * @param methodName
	 * @return
	 */
	private String getMethodNickName(String serviceName, String methodName) {
		String serviceNickName = propertiesLog.getValue(serviceName + ".name");
		methodName = methodName.toLowerCase();
		if (methodName.indexOf("create") > -1) {
			return "添加" + serviceNickName;
		}
		if (methodName.indexOf("update") > -1) {
			return "修改" + serviceNickName;
		}
		if (methodName.indexOf("delete") > -1) {
			return "删除" + serviceNickName;
		}

		return methodName;
	}

	/**
	 * 根据配置实例化日志记录
	 * 
	 * @param logServiceConfig
	 * @param logMethodConfig
	 * @return
	 */
	public LogBusinessEntity instantiationSysLogEntity(LogServiceConfig logServiceConfig, LogMethodConfig logMethodConfig) {
		LogBusinessEntity entity = new LogBusinessEntity();
		entity.setModeName(logServiceConfig.getNickName());
		entity.setMethodName(logMethodConfig.getNickName());
		entity.setOperType(logMethodConfig.getLogType());
		entity.setLogContent(logMethodConfig.getNickName());
		return entity;
	}

}
