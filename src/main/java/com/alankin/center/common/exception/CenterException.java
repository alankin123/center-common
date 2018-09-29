package com.alankin.center.common.exception;

import com.alankin.center.common.vo.BaseRespVO;

public class CenterException extends Exception {

	private static final long serialVersionUID = 1L;

	private int code;

	private String msg;

	public BaseRespVO getRespVO() {
		return new BaseRespVO(code, msg);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public CenterException() {
		super();
		this.code = 999;
	}

	public CenterException(String message) {
		super(message);
		this.code = 999;
		this.msg = message;
	}

	public CenterException(int code, String message) {
		super(message);
		this.msg = message;
		this.code = code;
	}

}
