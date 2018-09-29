package com.alankin.center.common.ftp;

import java.io.IOException;

/**
 * FTP文件上传下载服务实现.<p>
 * @author lansongtao
 * @Date 2014年8月8日
 * @since 1.0
 */
public interface IFileFtp {
	
	/** 文件编码格式.*/
	public static final String FILE_CHARSET = "UTF-8";
	
	/*********************************** 上传文件状态 ********************************/
	/** 上传文件状态: 0-上传成功  */
	public static final int UPLOAD_SUCCESS = 0;
	
	/** 上传文件状态: -1-上传失败  */
	public static final int UPLOAD_FAILED = -1;
	
	/** 上传文件状态: 1-上传连接失败 */
	public static final int UPLOAD_CONNECT_FAILED = 1;
		
	/** 上传文件状态: 2-上传超时 */
	public static final int UPLOAD_TIME_OUT = 2;
	
	/** 上传文件状态: 3-权限不足 */
	public static final int UPLOAD_INSUFF_ERROR = 3;
	
	/** 上传文件状态: 4-上传下载文件不存在	 */
	public static final int UPLOAD_NO_FILE = 4;
	
	/** 上传文件状态: 5-上传下载文件已存在 	 */
	public static final int UPLOAD_FILE_EXIST = 5;

	/**
	 * 获取FTP服务器连接信息.
	 * @return
	 */
	FtpServerInfo getFtpServer();
	
	/**
	 * 登录FTP服务器.
	 * @return			是否登录成功
	 */
	boolean login();

	/**
	 * 退出FTP服务器.
	 * @return			是否退出成功
	 */
	void logout();
	
	/**
	 * 上传文件到FTP服务器，支持断点续传
	 * 
	 * @param localFilePath
	 *            本地文件名称，绝对路径
	 * @param remoteFilePath
	 *            远程文件路径，使用/home/directory1/subdirectory/file.ext或是 http://www.guihua.org /subdirectory/file.ext
	 *            按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
	 * @return 上传结果 -1上传文件失败，0成功，1远程创建目录失败，2远程删除文件失败，
	 * @throws IOException
	 */
	int upload(String localFilePath, String remoteFilePath);

	/**
	 * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
	 * 
	 * @param remote
	 *            远程文件路径
	 * @param local
	 *            本地文件路径
	 * @return 下载的状态 -1文件下载失败，0文件下载成功，1服务器文件不存在，2本地文件大于远程文件
	 * @throws IOException
	 */
	int download(String remoteFilePath, String localFilePath);


}
