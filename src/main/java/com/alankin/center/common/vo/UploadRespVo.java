package com.alankin.center.common.vo;

public class UploadRespVo extends BaseRespVO {
	private static final long serialVersionUID = 1L;

	private String filePath;

	public UploadRespVo() {
		super();
	}

	public UploadRespVo(String filePath) {
		super();
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
