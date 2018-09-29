package com.alankin.center.common.log;

import java.util.Map;

public class LogServiceConfig {
	/* 名字 */
	private String serviceName;
	/* 昵称 */
	private String nickName;
	/* 方法集 */
	private Map<String, LogMethodConfig> methodMap;
	/* 实体字段名 */
	private Map<String, String> entityFieldNameMap;

	/* 实体主键ID字段名 */
	private String idFieldName;

	public String getIdFieldName() {
		return idFieldName;
	}

	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Map<String, LogMethodConfig> getMethodMap() {
		return methodMap;
	}

	public void setMethodMap(Map<String, LogMethodConfig> methodMap) {
		this.methodMap = methodMap;
	}

	public Map<String, String> getEntityFieldNameMap() {
		return entityFieldNameMap;
	}

	public void setEntityFieldNameMap(Map<String, String> entityFieldNameMap) {
		this.entityFieldNameMap = entityFieldNameMap;
	}

	@Override
	public String toString() {
		return "LogServiceConfig [serviceName=" + serviceName + ", nickName=" + nickName + ", methodMap=" + methodMap + ", entityFieldNameMap="
		        + entityFieldNameMap + "]";
	}

}
