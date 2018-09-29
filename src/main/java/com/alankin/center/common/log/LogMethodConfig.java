package com.alankin.center.common.log;

public class LogMethodConfig {
	/* 名字 */
	private String methodName;
	/* 昵称 */
	private String nickName;
	/* 日志类型 */
	private long logType;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public long getLogType() {
		return logType;
	}

	public void setLogType(long logType) {
		this.logType = logType;
	}

	@Override
	public String toString() {
		return "LogMethodConfig [methodName=" + methodName + ", nickName=" + nickName + ", logType=" + logType + "]";
	}

}
