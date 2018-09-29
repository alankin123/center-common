package com.alankin.center.common.threadPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alankin.center.common.global.MapCache;
import com.alankin.center.common.service.ThreadContext;

public class ProxyExecuteThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ProxyExecuteThread.class);

	private Object obj;
	private String methodName;
	private Object[] args;

	public ProxyExecuteThread(Object obj, String methodName, Object... args) {
		super();
		this.obj = obj;
		this.methodName = methodName;
		this.args = args;
	}

	@Override
	public void run() {
		Method method = null;
		try {
			method = MapCache.getMethod(obj, methodName);
			method.invoke(obj, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			ThreadContext.unbindContext();
		}

	}

}
