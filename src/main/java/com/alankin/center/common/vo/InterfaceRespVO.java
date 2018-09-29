/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.vo;

/**
 * 查询视图基类
 * <p>
 * 
 * @author lansongtao
 * @Date 2015年10月8日
 * @since 1.0
 */
public class InterfaceRespVO extends BaseRespVO {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 响应内容 */
	private String respData;

	public InterfaceRespVO() {
	}

	public InterfaceRespVO(BaseRespVO base) {
		this.setState(base.getState());
		this.setMsg(base.getMsg());
	}

	public InterfaceRespVO(int state, String msg) {
		this.setState(state);
		this.setMsg(msg);
	}

	public String getRespData() {
		return respData;
	}

	public void setRespData(String respData) {
		this.respData = respData;
	}

	@Override
	public String toString() {
		return "InterfaceRespVO [respData=" + respData + ", toString()=" + super.toString() + "]";
	}

}