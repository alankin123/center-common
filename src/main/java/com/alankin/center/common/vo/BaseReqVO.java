/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.vo;

import java.io.Serializable;

/**
 * 查询视图基类
 * <p>
 * 
 * @author lansongtao
 * @Date 2015年10月8日
 * @since 1.0
 */
public class BaseReqVO extends BaseVO implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 数据权限代码 */
	public String authCode;

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	@Override
	public String toString() {
		return "BaseCityEntity [authCode=" + authCode + ", toString()=" + super.toString() + "]";
	}

}
