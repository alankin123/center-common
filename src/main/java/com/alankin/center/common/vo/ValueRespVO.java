/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.vo;

import java.io.Serializable;

/**
 * 公共响应VO，MAP类型
 * <p>
 * 
 * @author lansongtao
 * @Date 2015年10月8日
 * @since 1.0
 */
public class ValueRespVO extends BaseRespVO implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	Object aaData;

	public Object getAaData() {
		return aaData;
	}

	public void setAaData(Object aaData) {
		this.aaData = aaData;
	}

	public ValueRespVO(Object value) {
		super();
		this.aaData = value;
	}

	public ValueRespVO() {
		super();
	}

	public ValueRespVO(int state, String msg) {
		super(state, msg);
	}

	public static ValueRespVO error() {
		return new ValueRespVO(99, "未知错误！");
	}

	@Override
	public String toString() {
		return "ValueRespVO [aaData=" + aaData + ", toString()=" + super.toString() + "]";
	}

}