package com.alankin.center.common.exception;

public class CenterValidateException extends CenterException {

	private static final long serialVersionUID = 1L;

	public CenterValidateException() {
		super();
	}

	public CenterValidateException(int code, String message) {
		super(code, message);
	}

	public CenterValidateException(String message) {
		super(message);
	}

}
