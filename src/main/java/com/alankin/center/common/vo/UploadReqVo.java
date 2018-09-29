package com.alankin.center.common.vo;

public class UploadReqVo extends BaseReqVO {
	private static final long serialVersionUID = 1L;

	private String file_FILENAME;
	private byte[] file;
	private String savePath;

	public String getFile_FILENAME() {
		return file_FILENAME;
	}

	public void setFile_FILENAME(String file_FILENAME) {
		this.file_FILENAME = file_FILENAME;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

}
