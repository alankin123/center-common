/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共响应VO，MAP类型
 * <p>
 * 
 * @author lansongtao
 * @Date 2015年10月8日
 * @since 1.0
 */
public class MapRespVO extends BaseRespVO implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private int msgStatus=0;

	Map<String, Object> aaData = new HashMap<String, Object>();

	public Map<String, Object> getAaData() {
		return aaData;
	}

	public void setAaData(Map<String, Object> aaData) {
		this.aaData = aaData;
	}

	public MapRespVO() {
	}

	public MapRespVO(int state, String msg) {
		super(state, msg);
	}

	public static MapRespVO error() {
		return new MapRespVO(99, "未知错误！");
	}

	@Override
	public String toString() {
		return "MapRespVO [aaData=" + aaData + ", toString()=" + super.toString() + "]";
	}

	public int getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}

}