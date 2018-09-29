package com.alankin.center.common.service;

import com.alankin.center.common.entity.SessionUser;

public class ThreadContext {

	/* 登录用户信息 */
	private static final ThreadLocal<SessionUser> SESSION_LOGIN_USER = new InheritableThreadLocal<SessionUser>();

	/* 请求客户端的IP */
	private static final ThreadLocal<String> REQUEST_CLIENT_IP = new InheritableThreadLocal<String>();

	/**
	 * 只有通过前端网关调用的方法才能取到session信息
	 * 
	 * @return
	 */
	public static SessionUser getSessionloginUser() {
		return getSessionloginUser(true);
	}

	/**
	 * 只有通过前端网关调用的方法才能取到session信息
	 * 
	 * @param isValidate
	 * @return
	 */
	public static SessionUser getSessionloginUser(boolean isValidate) {
		SessionUser sessionUser = SESSION_LOGIN_USER.get();
		if (isValidate && sessionUser == null) {
			throw new RuntimeException("登录信息失效，请重新登录!");
		}
		return sessionUser;
	}

	public static void setSessionloginUser(SessionUser sessionUser) {
		SESSION_LOGIN_USER.set(sessionUser);
	}

	public static String getRequestClientIp() {
		return REQUEST_CLIENT_IP.get();
	}

	public static void setRequestClientIp(String requestClientIp) {
		REQUEST_CLIENT_IP.set(requestClientIp);
	}

	/**
	 * 清除本地线程缓存，避免线程池复用线程导致混乱s
	 * 
	 */
	public static void unbindContext() {
		SESSION_LOGIN_USER.remove();
		REQUEST_CLIENT_IP.remove();
	}

}
