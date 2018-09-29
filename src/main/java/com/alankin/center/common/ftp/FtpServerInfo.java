package com.alankin.center.common.ftp;

import java.io.Serializable;

import com.alankin.center.common.utils.EmptyUtils;

/**
 * FTP服务器连接信息.<p>
 * @author lansongtao
 * @Date 2014年8月8日
 * @since 1.0
 */
public class FtpServerInfo implements Serializable {
	
	/** 文件分隔符.*/
	public static final String FILE_PATH_SPLIT = "/";

	private static final long serialVersionUID = -7859791911318811397L;

	/** ftp服务器ip 从配置文件中获取 **/
	private String hostname;
	
	/** ftp 端口 从配置文件中获取 **/
	private int port = 21;
	
	/** ftp 用户名 从配置文件中获取 **/
	private String username;
	
	/** ftp 密码 从配置文件中获取 **/
	private String password;
	
	/** 远程ftp存放文件目录路径 **/
	private String remoteFileDir = FILE_PATH_SPLIT;
	
	/** 本地生成准备上传文件路径 **/
	private String localUpload;
	
	/** 本地下载文件路径 **/
	private String localDownload;

	/** 是否被动方式 */
	private boolean isPassiveMode = true;

	/** 是否备份本地下载和上传文件 */
	private boolean isBackup = true;
	
	/** 下载或上传时,如果文件已存在是否断点续传 */
	private boolean isBreakpointTrans = false;
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 添加分隔符
	 */
    private String addLastSplit(String filePath) {
    	if (EmptyUtils.isEmpty(filePath)) {
    		return "/temp" + FILE_PATH_SPLIT;
    	}
	    if (!filePath.endsWith(FILE_PATH_SPLIT)) {
	    	filePath = filePath + FILE_PATH_SPLIT;
		}
	    return filePath;
    }
	
	public String getRemoteFileDir() {
		return addLastSplit(remoteFileDir);
	}

	public void setRemoteFileDir(String remoteFileDir) {
		this.remoteFileDir = remoteFileDir;
	}
	
	public String getLocalUpload() {
		return addLastSplit(localUpload);
	}

	public void setLocalUpload(String localUpload) {
		this.localUpload = localUpload;
	}

	public String getLocalDownload() {
		return addLastSplit(localDownload);
	}

	public void setLocalDownload(String localDownload) {
		this.localDownload = localDownload;
	}

	public boolean isPassiveMode() {
		return isPassiveMode;
	}

	public void setPassiveMode(boolean isPassiveMode) {
		this.isPassiveMode = isPassiveMode;
	}

	public boolean isBackup() {
		return isBackup;
	}

	public void setBackup(boolean isBackup) {
		this.isBackup = isBackup;
	}

	public boolean isBreakpointTrans() {
		return isBreakpointTrans;
	}

	public void setBreakpointTrans(boolean isBreakpointTrans) {
		this.isBreakpointTrans = isBreakpointTrans;
	}

	/**
	 * 构造方法.
	 */
    public FtpServerInfo() {
	    super();
    }

	/**
	 * 构造方法.
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param remoteFileDir
	 */
    public FtpServerInfo(String hostname, int port, String username, String password, String remoteFileDir) {
	    this.hostname = hostname;
	    this.port = port;
	    this.username = username;
	    this.password = password;
	    this.remoteFileDir = remoteFileDir;
    }

	@Override
    public String toString() {
	    return "FtpServerInfo [hostname=" + hostname + ", port=" + port + ", username=" + username + ", remoteFileDir="
	            + remoteFileDir + ", localUpload=" + localUpload + ", localDownload=" + localDownload + "]";
    }

}
