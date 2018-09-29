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
public class BaseRespVO extends BaseVO implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 响应码 */
	private int state = 0;

	/** 响应信息 */
	private String msg = "success";

	public boolean isSuccess() {
		return 0 == this.state;
	}

	public BaseRespVO() {
	}

	public BaseRespVO(int state, String msg) {
		this.state = state;
		this.msg = msg;
	}

	public static BaseRespVO error() {
		return new BaseRespVO(99, "未知错误！");
	}

	public static BaseRespVO error(String msg) {
		return new BaseRespVO(99, msg);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "BaseRespVO [state=" + state + ", msg=" + msg + "]";
	}

}