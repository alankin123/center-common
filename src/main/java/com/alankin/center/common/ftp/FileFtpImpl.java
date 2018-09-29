package com.alankin.center.common.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP文件上传下载服务实现.<p>
 * @author lansongtao
 * @Date 2014年8月8日
 * @since 1.0
 */
public class FileFtpImpl implements IFileFtp {
	
	/** 缓冲长度.*/
    private static final int BUFFER_SIZE = 1024;

	/** 文件名编码.*/
    private static final String FILE_NAME_CHARSET = "GBK";

	/** 日志. */
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	/** Ftp客户端连接 */
	private FTPClient ftpClient;
	
	/** Ftp连接服务端信息 */
	private FtpServerInfo ftpServer;

	/** 初始目录 */
	private String parentDir;
	
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	public void setFtpServer(FtpServerInfo ftpServer) {
		this.ftpServer = ftpServer;
	}
	
	/**
	 * 构造方法.
	 */
    public FileFtpImpl() {
	    super();
    }
    
	/**
	 * 构造方法.
	 * @param ftpClient			Ftp客户端连接
	 * @param ftpServer			Ftp连接服务端信息 
	 */
    public FileFtpImpl(FTPClient ftpClient, FtpServerInfo ftpServer) {
	    this.ftpClient = ftpClient;
	    this.ftpServer = ftpServer;
    }
    
    @Override
    public FtpServerInfo getFtpServer() {
        return this.ftpServer;
    }
    
    /**
     * 添加上级路径.
     * @param path			相对路径
     * @return
     */
	private String formatPath(String path) {
		path = parentDir + "/" + path;
		return path.replaceAll("//*", "/");
	}
	
	/**
     * 删除文件
     * @param fileName
     * @return
     */
    public boolean deleteFile(String filePath) {
    	File file = new File(filePath);
    	if (!file.exists()) {
    		return false;
    	}
    	// 如果是文件,则直接删除
    	if (file.isFile()) {
    		return file.delete();
    	}
    	// 如果是文件夹,则递归删除
		if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0, length = filelist.length; i < length; i++) {
				File delfile = new File(filePath + "/" + filelist[i]);
				if (!delfile.isDirectory()) {
					delfile.delete();
				} else if (delfile.isDirectory()) {
					deleteFile(filePath + "/" + filelist[i]);
				}
			}
			file.delete();
		}
    	return false;
    }

	@Override
	public boolean login() {
		try {
			ftpClient.connect(ftpServer.getHostname(), ftpServer.getPort());
			boolean result = ftpClient.login(ftpServer.getUsername(), ftpServer.getPassword());
			if (result) {
				int reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
					LOGGER.error(String.format("ftp登录[%s:%d]失败", ftpServer.getHostname(), ftpServer.getPort()));
				}
				ftpClient.setControlEncoding(IFileFtp.FILE_CHARSET);
				// 设置PassiveMode传输
				if (ftpServer.isPassiveMode()) {
					ftpClient.enterLocalPassiveMode();
				} else {
					ftpClient.enterLocalActiveMode();
				}
				// 设置以二进制流的方式传输
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				parentDir = ftpClient.printWorkingDirectory();
//				parentDir = ftpServer.getRemoteFileDir();
			} else {
				LOGGER.error("ftpClient.login返回失败");
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("文件上传失败", e);
			return false;
		}
		LOGGER.debug(String.format("ftp登录[%s:%d]成功", ftpServer.getHostname(), ftpServer.getPort()));
		return true;
	}

	@Override
	public void logout() {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
				LOGGER.debug("ftp 退出成功");
			}
		} catch (IOException e) {
			LOGGER.error("ftp 退出失败", e);
		}
	}

	@Override
	public int upload(String localFilePath, String remotePath) {
		synchronized (remotePath) {
			int result = 0;
			try {
				//将工作目录置为初始目录
				ftpClient.changeWorkingDirectory(parentDir);
				// 对远程目录的处理
				String remoteFilePath = formatPath(remotePath);
				if (remoteFilePath.contains("/")) {
//					remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
					// 创建服务器远程目录结构，创建失败直接返回
					if (!createDirecroty(remoteFilePath)) {
						return UPLOAD_INSUFF_ERROR;
					}
				}
				// 检查远程是否存在文件
				FTPFile[] files = ftpClient.listFiles(new String(remoteFilePath.getBytes(FILE_NAME_CHARSET), IFileFtp.FILE_CHARSET));
				if (files.length == 1) {
//					result = breakPointUploadTransFile(localFilePath, remotePath, remoteFilePath, files);
					result = overlayFile(localFilePath, remoteFilePath);
				} else {
					if(uploadFile(remoteFilePath, new File(localFilePath), 0)) {
						result = UPLOAD_SUCCESS;
					}
				}
				LOGGER.debug(String.format("上传本地文件[%s]到ftp[%s]成功", localFilePath, remotePath));
			} catch (IOException e) {
				LOGGER.error(String.format("上传本地文件[%s]到ftp[%s]失败", localFilePath, remotePath), e);
				result = UPLOAD_FAILED;
			}
			return result;
        }
	}
	
	/**
	 * 覆盖已存在的文件
	 * @param localFilePath				本地文件路径
	 * @param remoteFilePath			远程文件路径
	 * @return
	 * @throws IOException
	 */
	private int overlayFile(String localFilePath, String remoteFilePath) throws IOException {
		LOGGER.info(String.format("远程文件[%s]已存在,先删除,再上传", remoteFilePath));
    	if (!ftpClient.deleteFile(remoteFilePath)) {
    		LOGGER.error(String.format("删除远程文件[%s]失败", remoteFilePath));
    		return UPLOAD_FILE_EXIST;
    	}
    	if(uploadFile(remoteFilePath, new File(localFilePath), 0)) {
    		return UPLOAD_SUCCESS;
		}
    	return UPLOAD_FAILED;
	}

	/**
	 * 断点续传
	 * @param localFilePath				本地文件路径
	 * @param remotePath				远程文件路径
	 * @param remoteFilePath			远程文件全路径
	 * @param files						文件列表
	 * @return
	 * @throws IOException
	 */
    @SuppressWarnings("unused")
	private int breakPointUploadTransFile(String localFilePath, String remotePath, String remoteFilePath,
            FTPFile[] files) throws IOException {
	    if (!ftpServer.isBreakpointTrans()) {
	    	LOGGER.info(String.format("远程文件[%s]已存在,先删除,再上传", remotePath));
	    	if (!ftpClient.deleteFile(remoteFilePath)) {
	    		LOGGER.error(String.format("删除远程文件[%s]失败", remotePath));
	    		return UPLOAD_FILE_EXIST;
	    	}
	    }
	    long remoteSize = files[0].getSize();
	    File f = new File(localFilePath);
	    long localSize = f.length();
	    if (remoteSize >= localSize) {
	    	LOGGER.info(String.format("远程文件[%s]已经存在,将覆盖原文件", remotePath));
	    	if (uploadFile(remoteFilePath, new File(localFilePath), 0)) {
	    		LOGGER.debug(String.format("上传本地文件[%s]到ftp[%s]成功", localFilePath, remotePath));
	    		return UPLOAD_SUCCESS;
	    	} else {
	    		return UPLOAD_FAILED;
	    	}
	    }
	    int result = 0;
	    // 尝试移动文件内读取指针,实现断点续传
	    if (uploadFile(remoteFilePath, f, remoteSize)) {
	    	result = UPLOAD_SUCCESS;
	    }

	    // 如果断点续传没有成功，则删除服务器上文件，重新上传
	    if (result != 0) {
	    	if (!ftpClient.deleteFile(remoteFilePath)) {
	    		return UPLOAD_INSUFF_ERROR;
	    	}
	    	if(uploadFile(remoteFilePath, f, 0)) {
	    		result = UPLOAD_SUCCESS;
	    	} else {
	    		result = UPLOAD_FAILED;
	    	}
	    }
	    return result;
    }

	/**
	 * 上传文件到服务器,新上传和断点续传
	 * 
	 * @param remoteFilePath
	 *            远程文件名，在上传之前已经将服务器工作目录做了改变
	 * @param localFile
	 *            本地文件File句柄，绝对路径
	 * @param remoteSize
	 *            断点续传长度
	 * @return
	 * @throws IOException
	 */
	private boolean uploadFile(String remoteFilePath, File localFile, long remoteSize) throws IOException {
		// 显示进度的上传
		long localSize = localFile.length();
		long step = localSize / 100;
		long process = 0;
		long localreadbytes = 0L;
		RandomAccessFile raf = null;
		OutputStream out = null;
		try {
			raf = new RandomAccessFile(localFile, "r");
			out = ftpClient.appendFileStream(new String(remoteFilePath.getBytes(FILE_NAME_CHARSET), IFileFtp.FILE_CHARSET));
			// 断点续传
			if (remoteSize > 0) {
				ftpClient.setRestartOffset(remoteSize);
				process = remoteSize / step;
				raf.seek(remoteSize);
				localreadbytes = remoteSize;
			}
			byte[] bytes = new byte[BUFFER_SIZE];
			int readLen = -1;
			while ((readLen = raf.read(bytes)) != -1) {
				out.write(bytes, 0, readLen);
				localreadbytes += readLen;
//				if (localreadbytes / step != process) {
//					process = localreadbytes / step;
//					LOGGER.debug("上传进度:" + process + "%");
//				}
				long nowProcess = localreadbytes / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0) {
						LOGGER.debug("上传进度：" + process + "%");
					}
				}
			}
			out.flush();
			out.close();
			return ftpClient.completePendingCommand();
        } finally {
        	if (raf != null) {
        		raf.close();
        	}
        	if (out != null) {
        		out.close();
        	}
        }
	}

	@Override
	public int download(String remotePath, String localFilePath) {
		synchronized (localFilePath) {
			try {
				//将工作目录置为初始目录
				ftpClient.changeWorkingDirectory(parentDir);
				String remoteFilePath = formatPath(remotePath);
				// 检查远程文件是否存在
				FTPFile[] files = ftpClient.listFiles(new String(remoteFilePath.getBytes(FILE_NAME_CHARSET), IFileFtp.FILE_CHARSET));
				if (files.length != 1) {
					LOGGER.error(String.format("远程文件[%s]不存在", remotePath));
					return UPLOAD_NO_FILE;
				}

				long lRemoteSize = files[0].getSize();
				File f = new File(localFilePath);
				boolean isExist = f.exists();
				if (!isExist) {
					// 如果不存在,则创建所在目录
					new File(localFilePath.substring(0, localFilePath.lastIndexOf("/"))).mkdirs();
				}
				// 本地存在文件，进行断点下载
				if (isExist && ftpServer.isBreakpointTrans()) {
					return breakPointDownloadTransFile(remotePath, localFilePath, remoteFilePath,
                            lRemoteSize, f);
				} else {
					// 如果存在,则先删除
					if (isExist) {
						deleteFile(localFilePath);
					}
					OutputStream out = new FileOutputStream(f);
					InputStream in = ftpClient.retrieveFileStream(new String(remoteFilePath.getBytes(FILE_NAME_CHARSET), IFileFtp.FILE_CHARSET));
					byte[] bytes = new byte[1024];
					long step = lRemoteSize / 100;
					long process = 0;
					long localSize = 0L;
					int c;
					while ((c = in.read(bytes)) != -1) {
						out.write(bytes, 0, c);
						localSize += c;
						long nowProcess = localSize / step;
						if (nowProcess > process) {
							process = nowProcess;
							if (process % 10 == 0)
								LOGGER.debug("下载进度：" + process + "%");
						}
					}
					in.close();
					out.close();
					boolean upNewStatus = ftpClient.completePendingCommand();
					if (upNewStatus) {
						LOGGER.debug(String.format("下载远程文件[%s]到本地[%s]成功", remotePath, localFilePath));
						return UPLOAD_SUCCESS;
					} else {
						return UPLOAD_FAILED;
					}
				}
			} catch (Exception e) {
				LOGGER.error(String.format("下载远程文件[%s]到本地[%s]失败", remotePath, localFilePath), e);
				return UPLOAD_FAILED;
			}
		}
	}

	
	/**
     * 断点续传.
	 * @param localFilePath				本地文件路径
	 * @param remotePath				远程文件路径
	 * @param remoteFilePath			远程文件全路径
	 * @param f							文件列表
     * @return
     * @throws Exception
     */
    private int breakPointDownloadTransFile(String remotePath, String localFilePath,
            String remoteFilePath, long lRemoteSize, File f) throws Exception {
	    long localSize = f.length();
	    // 判断本地文件大小是否大于远程文件大小
	    if (localSize >= lRemoteSize) {
	    	LOGGER.debug(String.format("本地文件[%s]大于远程文件[%s]，断点下载中止", localFilePath, remotePath));
	    	return UPLOAD_FILE_EXIST;
	    }
	    // 进行断点续传，并记录状态
	    FileOutputStream out = new FileOutputStream(f, true);
	    ftpClient.setRestartOffset(localSize);
	    InputStream in = ftpClient.retrieveFileStream(new String(remoteFilePath.getBytes(FILE_NAME_CHARSET), IFileFtp.FILE_CHARSET));
	    byte[] bytes = new byte[1024];
	    long step = lRemoteSize / 100;
	    long process = localSize / step;
	    int c;
	    while ((c = in.read(bytes)) != -1) {
	    	out.write(bytes, 0, c);
	    	localSize += c;
	    	long nowProcess = localSize / step;
	    	if (nowProcess > process) {
	    		process = nowProcess;
	    		if (process % 10 == 0) {
	    			LOGGER.debug("下载进度：" + process + "%");
	    		}
	    	}
	    }
	    in.close();
	    out.close();
	    boolean isDo = ftpClient.completePendingCommand();
	    if (isDo) {
	    	return UPLOAD_SUCCESS;
	    } else {
	    	return UPLOAD_FAILED;
	    }
    }

	/**
	 * 创建FTP目录.
	 * @param remote			远程文件路径
	 * @return
	 * @throws IOException
	 */
	private boolean createDirecroty(String remote) throws IOException {
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!directory.equalsIgnoreCase("/")
		        && !ftpClient.changeWorkingDirectory(new String(directory.getBytes(FILE_NAME_CHARSET), IFileFtp.FILE_CHARSET))) {
			// 如果远程目录不存在，则递归创建远程服务器目录
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes(FILE_NAME_CHARSET), IFileFtp.FILE_CHARSET);
				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						LOGGER.debug(String.format("创建远程目录[%s]失败", subDirectory));
						return false;
					}
				}
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		IFileFtp fileFtp = new FileFtpImpl();
		FtpServerInfo ftpServer = new FtpServerInfo("172.20.8.188", 23, "ftpuser88", "111111", "/fund/bill/upload");
		String localFilePath = "E:/test.txt";
    	fileFtp.login();
    	String remoteFilePath = ftpServer.getRemoteFileDir() + localFilePath;
		int uploadStatus = fileFtp.upload(localFilePath, remoteFilePath);
		if (uploadStatus == UPLOAD_SUCCESS) {
			fileFtp.download(remoteFilePath, localFilePath);
		} 
    }

}
