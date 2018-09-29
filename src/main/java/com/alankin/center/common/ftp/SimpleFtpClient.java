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
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * Ftp上传工具
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: Shenzhen XGD SoftWare
 * </p>
 * @since 2015年10月20日
 * @version 1.0
 * @author WillYang
 */
public class SimpleFtpClient {

    private static final Logger logger = LoggerFactory.getLogger(SimpleFtpClient.class);

    /**
     * 上传成功
     */
    public static final int FTP_SUCCESS = 0;

    /**
     * 上传失败
     */
    public static final int FTP_FAIL = -1;

    /**
     * 上传超时
     */
    public static final int TIME_OUT = 1;

    /**
     * 连接失败
     */
    public static final int CONNECT_FAILED = 2;

    /**
     * 权限不足
     */
    public static final int INSUFF_ERROR = 3;

    private final int CONNECT_TIMEOUT = 3;

    private final String TRANS_ENCODING = "ISO-8859-1";

    private final String FILE_ENCODING = "GBK";

    private final String FILE_CHARSET = "UTF-8";

    private final int BUFFER_SIZE = 1024;

    private FTPClient client;

    private String parentDir;

    private SimpleFtpServerInfo serverInfo;

    public SimpleFtpClient(SimpleFtpServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public boolean login() {
        String logMsg =
            String.format("ftp://%s:%s@%s:%d", serverInfo.getUserName(), serverInfo.getPassword(),
                serverInfo.getHostName(), serverInfo.getPort());
        logger.debug(logMsg);
        try {
            client = new FTPClient();
            client.setConnectTimeout(CONNECT_TIMEOUT * 1000);
            client.connect(serverInfo.getHostName(), serverInfo.getPort());

            boolean result = client.login(serverInfo.getUserName(), serverInfo.getPassword());
            if (result) {
                int reply = client.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    client.disconnect();
                    logger.error("Ftp登录失败，用户名或密码错");
                    return false;
                }
                client.setControlEncoding(FILE_CHARSET);
                // 设置PassiveMode传输
                if (serverInfo.isPassiveMode()) {
                    client.enterLocalPassiveMode();
                }
                else {
                    client.enterLocalActiveMode();
                }
                // 设置文件传输方式
                if (serverInfo.isTransBinary()) {
                    client.setFileType(FTPClient.BINARY_FILE_TYPE);
                }
                else {
                    client.setFileType(FTPClient.ASCII_FILE_TYPE);
                }
                client.setKeepAlive(true);
                parentDir = client.printWorkingDirectory();
                logger.info("Working directory:" + parentDir);
                logger.info("Ftp登录成功");
                return true;
            }
            else {
                logger.error("Ftp登录返回失败");
                return false;
            }
        }
        catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    public void logout() {
        try {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
                logger.info("Ftp 退出成功，已断开连接");
            }

        }
        catch (IOException e) {
            logger.error("", e);
        }
    }

    /**
     * 
     * Description: 上传本地文件到远端指定目录
     * @author:yangxianqiang
     * @date: 2014年10月27日
     * @param localFile 本地文件路径
     * @param remotePath 远端目录
     * @param override 是否覆盖远端文件，true覆盖
     * @return
     */
    public int upload(String localFile, String remotePath, boolean override) {
        int result = FTP_SUCCESS;
        boolean uploadResult = true;
        long beginTime = System.currentTimeMillis();
        try {

            File file = new File(localFile);
            if (!file.exists()) {
                logger.error("上传文件不存在,文件名:" + localFile);
                return FTP_FAIL;
            }

            // 远程保存文件名
            String remoteFileName = file.getName();

            remotePath = formatPath(remotePath);

            client.changeWorkingDirectory(new String(parentDir.getBytes(FILE_ENCODING), TRANS_ENCODING));

            // 检查远端文件夹路径是否存在并创建文件夹并切换到工作目录
            if (!client.changeWorkingDirectory(new String(remotePath.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                if (!createDirectory(remotePath)) {
                    return INSUFF_ERROR;
                }
            }
            logger.info("当前工作目录:" + client.printWorkingDirectory());
            logger.info("上传文件,远端路径:" + remotePath);
            long remoteSize = 0;
            if (override) {
                logger.info("本地文件上传开始,文件名:" + localFile);
                uploadResult = uploadFile(file, remoteFileName, 0);
            }
            else {
                FTPFile[] ftpFiles =
                    client.listFiles(new String(remoteFileName.getBytes(FILE_ENCODING), TRANS_ENCODING));
                if (ftpFiles.length == 1) {
                    remoteSize = ftpFiles[0].getSize();
                    long localSize = file.length();
                    if (remoteSize == localSize) {
                        return FTP_SUCCESS;
                    }
                    else if (remoteSize > localSize) {
                        uploadResult = uploadFile(file, remoteFileName, 0);
                    }
                    else {
                        uploadResult = uploadFile(file, remoteFileName, remoteSize);
                    }
                }
                else {
                    uploadResult = uploadFile(file, remoteFileName, remoteSize);
                }
            }
            String logMsg = String.format("文件上传结束,用时:%dms", System.currentTimeMillis() - beginTime);
            logger.info(logMsg);
            if (!uploadResult) {
                result = FTP_FAIL;
            }
            result = FTP_SUCCESS;

        }
        catch (Exception e) {
            logger.error("", e);
            result = FTP_FAIL;
        }
        return result;
    }

    /**
     * 
     * Description: 上传文件，支持断点续传与上传进度显示 
     * @author: yangxianqiang
     * @date: 2014年10月27日
     * @param localFile
     * @param remoteFile
     * @param remoteSize
     * @return
     * @throws IOException
     */
    private boolean uploadFile(File localFile, String remoteFileName, long remoteSize) throws IOException {
        long totalBytes = localFile.length();
        long readBytes = 0L;
        RandomAccessFile randomAccessFile = null;
        OutputStream outputStream = null;
        try {
            randomAccessFile = new RandomAccessFile(localFile, "r");
            outputStream = null;

            // 断点续传
            if (remoteSize > 0) {
                outputStream =
                    client.appendFileStream(new String(remoteFileName.getBytes(FILE_ENCODING), TRANS_ENCODING));
                client.setRestartOffset(remoteSize);
                randomAccessFile.seek(remoteSize);
                readBytes = remoteSize;
            }
            else {
                outputStream =
                    client.storeFileStream(new String(remoteFileName.getBytes(FILE_ENCODING), TRANS_ENCODING));
            }
            byte[] tmpByte = new byte[BUFFER_SIZE];
            int readLen = -1;
            while ((readLen = randomAccessFile.read(tmpByte)) != -1) {
                outputStream.write(tmpByte, 0, readLen);
                readBytes += readLen;
                logger.debug("上传进度:" + String.format("%.2f%%", (readBytes / 1.00 / totalBytes) * 100));
            }
            outputStream.flush();
            outputStream.close();
            outputStream = null;
            return client.completePendingCommand();
        }
        finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    /**
     * 
     * Description: 下载远端文件到指定文件目录
     * @author: yangxianqiang
     * @date: 2014年10月27日
     * @param localPath 本地保存路径
     * @param remoteFile 远程文件名(含路径)
     * @param override 是否覆盖本地文件，true覆盖
     * @return
     */
    public int download(String localPath, String remoteFile, boolean override) {
        int result = FTP_SUCCESS;
        boolean downloadResult = false;
        long beginTime = System.currentTimeMillis();
        try {
            if (!localPath.endsWith("/")) {
                localPath += "/";
            }

            // 检查本地路径是否存在，不存在则创建路径
            File localFile = new File(localPath);
            if (!localFile.exists()) {
                if (!localFile.mkdirs()) {
                    logger.error("创建本地目录失败,本地路径为:" + localPath);
                    return INSUFF_ERROR;
                }
            }
            File rFile = new File(remoteFile);
            String fileName = rFile.getName();
            if ("".equals(fileName)) {
                logger.error("下载文件路径出错,文件路径格式:/home/ftpuser/example.txt");
                return FTP_FAIL;
            }
            String localFilePath = localPath + fileName;

            remoteFile = formatPath(remoteFile);

            // Ftp切换到下载目录
            String remotePath = remoteFile.substring(0, remoteFile.lastIndexOf("/") + 1);
            if (!client.changeWorkingDirectory(new String(remotePath.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                logger.error("远程目录不存在,远程目录:" + remotePath);
                return FTP_FAIL;
            }

            FTPFile[] ftpFiles = client.listFiles(new String(fileName.getBytes(FILE_ENCODING), TRANS_ENCODING));
            if (ftpFiles.length != 1) {
                logger.error("远程文件不存在,远程文件:" + remoteFile);
                return FTP_FAIL;
            }
            localFile = new File(localFilePath);
            long localSize = 0L;
            if (localFile.exists()) {
                if (override) {
                    logger.info("文件下载开始,文件名:" + localFilePath);
                    downloadResult = downloadFile(localFile, ftpFiles[0], 0);
                }
                else {
                    localSize = localFile.length();
                    if (localSize == ftpFiles[0].getSize()) {
                        logger.info("本地文件已存在,文件名:" + localFilePath);
                        return FTP_SUCCESS;
                    }
                    else if (localSize > ftpFiles[0].getSize()) {
                        logger.info("覆盖文件下载开始,文件名:" + localFilePath);
                        downloadResult = downloadFile(localFile, ftpFiles[0], 0);
                    }
                    else {
                        logger.info("文件下载开始(断点续传),文件名:" + localFilePath);
                        downloadResult = downloadFile(localFile, ftpFiles[0], localSize);
                    }
                }
            }
            else {
                logger.info("文件下载开始,文件名:" + localFilePath);
                downloadResult = downloadFile(localFile, ftpFiles[0], 0);
            }
            String logMsg = String.format("文件下载结束,用时:%dms", System.currentTimeMillis() - beginTime);
            logger.info(logMsg);
            if (!downloadResult) {
                result = FTP_FAIL;
            }
            result = FTP_SUCCESS;

        }
        catch (Exception e) {
            logger.error("", e);
            result = FTP_FAIL;
        }
        return result;
    }

    /**
     * 
     * Description: 下载远端文件夹下所有文件
     * @author: yangxianqiang
     * @date: 2014年10月27日
     * @param localPath 本地保存路径
     * @param remotePath 远端文件夹路径
     * @param filesFliter 过滤下载文件名格式，为空不过滤，下载全部文件,下载以example开头,以end结束的正则表达式为 "(^example)([\\s\\S]*)(end)$"
     * @param override 是否覆盖已下载文件
     * @param recursion 文件夹是否递归下载
     * @return
     */
    public int downloadDirectory(String localPath, String remotePath, String[] filesFliter, boolean override,
        boolean recursion) {
        int result = FTP_SUCCESS;
        int tmpResult = FTP_SUCCESS;
        try {
            if (!localPath.endsWith("/")) {
                localPath += "/";
            }
            remotePath = formatPath(remotePath);
            if (!remotePath.endsWith("/")) {
                remotePath += "/";
            }

            if (!client.changeWorkingDirectory(new String(remotePath.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                logger.error("远程目录不存在,目录:" + remotePath);
                return FTP_FAIL;
            }

            if (remotePath.startsWith(parentDir)) {
                if ("/".equals(parentDir)) {
                    remotePath = remotePath.substring(1);
                }
                else {
                    remotePath = remotePath.replace(parentDir, "");
                }
            }

            File localFile = new File(localPath);
            if (!localFile.exists()) {
                if (!localFile.mkdirs()) {
                    logger.error("创建本地目录失败,本地路径为:" + localPath);
                    return INSUFF_ERROR;
                }
            }

            FTPFile[] ftpFiles = client.listFiles();
            for (int i = 0; i < ftpFiles.length; i++) {
                FTPFile ftpFile = ftpFiles[i];
                if (".".equals(ftpFile.getName()) || "..".equals(ftpFile.getName())) {
                    continue;
                }
                String tmpLocalPath = localPath;
                String tmpRemotePath = remotePath + ftpFile.getName();
                if (ftpFile.isDirectory()) {
                    if (recursion) {
                        tmpLocalPath = localPath + ftpFile.getName();
                        tmpResult = downloadDirectory(tmpLocalPath, tmpRemotePath, filesFliter, override, recursion);
                        if (tmpResult != FTP_SUCCESS) {
                            result = tmpResult;
                        }
                    }
                    continue;
                }
                if (filesFliter != null) {
                    if (!fileFliter(ftpFile.getName(), filesFliter)) {
                        logger.debug(ftpFile.getName() + ",不满足过滤条件");
                        continue;
                    }
                }
                tmpResult = download(tmpLocalPath, tmpRemotePath, override);
                if (tmpResult != FTP_SUCCESS) {
                    result = tmpResult;
                }
            }

        }
        catch (Exception e) {
            logger.error("", e);
            result = FTP_FAIL;
        }
        return result;
    }

    /**
     * 
     * Description: 下载文件，支持断点续传与下载进度显示 
     * @author: yangxianqiang
     * @date: 2014年10月27日
     * @param localFile
     * @param ftpFile
     * @param localSize
     * @return
     * @throws IOException
     */
    private boolean downloadFile(File localFile, FTPFile ftpFile, long localSize) throws IOException {
        boolean result = true;
        long totalBytes = ftpFile.getSize();
        long readBytes = 0L;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            inputStream =
                client.retrieveFileStream(new String(ftpFile.getName().getBytes(FILE_ENCODING), TRANS_ENCODING));
            if (localSize > 0) {
                fileOutputStream = new FileOutputStream(localFile, true);
                client.setRestartOffset(localSize);
                readBytes = localSize;
            }
            else {
                fileOutputStream = new FileOutputStream(localFile, false);
            }
            byte[] tmpByte = new byte[BUFFER_SIZE];
            int readLen = -1;
            while ((readLen = inputStream.read(tmpByte)) != -1) {
                fileOutputStream.write(tmpByte, 0, readLen);
                readBytes += readLen;
                logger.info("下载进度:" + String.format("%.2f%%", (readBytes / 1.00 / totalBytes) * 100));
            }
            fileOutputStream.flush();
            // 关闭此处，不会等待20s
            inputStream.close();
            inputStream = null;
            result = client.completePendingCommand();
            return result;
        }
        finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 
     * Description: 递归创建Ftp目录,确保ftp有权限创建目录,不能跳出父目录 
     * @author: yangxianqiang
     * @date: 2014年10月27日
     * @param remotPath
     * @return
     */
    private boolean createDirectory(String remotPath) {
        try {
            if (!remotPath.endsWith("/")) {
                remotPath += "/";
            }
            if (remotPath.startsWith(parentDir)) {
                if ("/".equals(parentDir)) {
                    remotPath = remotPath.substring(1);
                }
                else {
                    remotPath = remotPath.replaceAll(parentDir, "");
                }
            }
            int start = 0;
            String tmpPath = remotPath;
            if (remotPath.startsWith("/")) {
                start = 1;
                tmpPath = remotPath.substring(1);
            }
            else {
                start = 0;
            }
            int end = remotPath.indexOf("/", start);
            if (!client.changeWorkingDirectory(new String(tmpPath.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                while (true) {
                    String subDirectory =
                        new String(remotPath.substring(start, end).getBytes(FILE_ENCODING), TRANS_ENCODING);
                    if (!client
                        .changeWorkingDirectory(new String(subDirectory.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                        if (client.makeDirectory(subDirectory)) {
                            client.changeWorkingDirectory(new String(subDirectory.getBytes(FILE_ENCODING),
                                TRANS_ENCODING));
                        }
                        else {
                            logger.debug("创建目录失败, 目录:" + subDirectory);
                            return false;
                        }
                    }

                    start = start + subDirectory.length() + 1;
                    end = remotPath.indexOf("/", start);

                    // 检查所有目录是否创建完毕
                    if (end <= start) {
                        break;
                    }
                }
            }
            return true;

        }
        catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    /**
     * 
     * Description: 过滤文件列表
     * @author: yangxianqiang
     * @date: 2014年10月27日
     * @param fileName
     * @param fliters
     * @return
     */
    private boolean fileFliter(String fileName, String[] fliters) {
        for (int i = 0; i < fliters.length; i++) {
            if (fileName.matches(fliters[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * Description: 删除远端文件 
     * @author: yangxianqiang
     * @date: 2014年10月27日
     * @param remoteFile 远端文件路径含文件名
     * @return
     */
    public int deleteFile(String remoteFile) {
        int result = FTP_SUCCESS;
        try {
            remoteFile = formatPath(remoteFile);
            String remotePath = remoteFile;
            if (remoteFile.length() > 1) {
                remotePath = remoteFile.substring(0, remoteFile.lastIndexOf("/"));
            }
            client.changeWorkingDirectory(new String(parentDir.getBytes(FILE_ENCODING), TRANS_ENCODING));
            if (!client.changeWorkingDirectory(new String(remotePath.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                logger.error("远端文件路径不存在,远端路径:" + remotePath);
                return FTP_FAIL;
            }
            FTPFile[] ftpFiles = client.listFiles(new String(remoteFile.getBytes(FILE_ENCODING), TRANS_ENCODING));
            if (ftpFiles.length != 1) {
                logger.info("远端文件不存在，文件名:" + remoteFile);
                return FTP_SUCCESS;
            }
            if (!client.deleteFile(new String(ftpFiles[0].getName().getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                logger.error("删除远端文件失败，文件名:" + remoteFile);
                result = FTP_FAIL;
            }
            else {
                logger.info("删除远端文件成功，文件名:" + remoteFile);
                result = FTP_SUCCESS;
            }
        }
        catch (IOException e) {
            logger.error("", e);
            result = FTP_FAIL;
        }
        return result;
    }

    /**
     * 
     * Description: 删除远端文件路径 
     * @author:yangxianqiang
     * @date: 2014年10月27日
     * @param remotePath
     * @return
     */
    public int deleteDirectory(String remotePath) {
        int result = FTP_SUCCESS;
        int tmpResult = FTP_SUCCESS;
        try {
            remotePath = formatPath(remotePath);
            if (!remotePath.endsWith("/")) {
                remotePath += "/";
            }
            client.changeWorkingDirectory(new String(parentDir.getBytes(FILE_ENCODING), TRANS_ENCODING));
            if (!client.changeWorkingDirectory(new String(remotePath.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                logger.info("远端文件路径不存在,远端路径:" + remotePath);
                return FTP_SUCCESS;
            }

            if (remotePath.startsWith(parentDir)) {
                if ("/".equals(parentDir)) {
                    remotePath.substring(1);
                }
                else {
                    remotePath = remotePath.replace(parentDir, "");
                }
            }

            FTPFile[] ftpFiles = client.listFiles();
            for (int i = 0; i < ftpFiles.length; i++) {
                FTPFile ftpFile = ftpFiles[i];
                if (".".equals(ftpFile.getName()) || "..".equals(ftpFile.getName())) {
                    continue;
                }
                String tmpRemotePath = remotePath + ftpFile.getName();
                if (ftpFile.isDirectory()) {
                    tmpResult = deleteDirectory(tmpRemotePath);
                    if (FTP_SUCCESS != tmpResult) {
                        result = tmpResult;
                    }
                }
                else {
                    tmpResult = deleteFile(tmpRemotePath);
                    if (FTP_SUCCESS != tmpResult) {
                        result = tmpResult;
                    }
                }
            }
            remotePath = formatPath(remotePath);
            if (!client.removeDirectory(new String(remotePath.getBytes(FILE_ENCODING), TRANS_ENCODING))) {
                logger.error("删除远端路径失败, 远端路径:" + remotePath);
                result = FTP_FAIL;
            }
            else {
                logger.info("删除远端路径成功，远端路径:" + remotePath);
                result = FTP_SUCCESS;
            }

        }
        catch (IOException e) {
            logger.error("", e);
            result = FTP_FAIL;
        }
        return result;
    }

    /**
     * 
     * Description: 格式化文件路径 
     * @author: yangxianqiang
     * @date: 2014年11月1日
     * @param path
     * @return 格式化后的文件路径
     */
    private String formatPath(String path) {
        path = parentDir + "/" + path;
        String result = path.replaceAll("//*", "/");
        return result;
    }

}
