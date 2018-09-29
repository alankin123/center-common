package com.alankin.center.common.ftp;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * <p>
 * Description: 请简述功能介绍
 * </p>
 * Ftp工具配置项，默认上传/下载使用被动方式与二进制流传输
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
public class SimpleFtpServerInfo {

    private String userName = "anonymous";

    private String password = "anonymous";

    private String hostName;

    private int port = 21;

    /**
     * 上传方式是否为passive true为被动模式
     */
    private boolean passiveMode = false;

    /**
     * 文件传输方式设置，默认为二进制 true,false为Ascii 
     */
    private boolean transBinary = true;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public boolean isTransBinary() {
        return transBinary;
    }

    public void setTransBinary(boolean transBinary) {
        this.transBinary = transBinary;
    }

    public SimpleFtpServerInfo() {
    }

    public SimpleFtpServerInfo(SimpleFtpServerInfo serverInfo) {
        this.hostName = serverInfo.getHostName();
        this.port = serverInfo.getPort();
        this.userName = serverInfo.getUserName();
        this.password = serverInfo.getPassword();
        this.passiveMode = serverInfo.isPassiveMode();
        this.transBinary = serverInfo.isTransBinary();
    }

    /**
     * like ftp://willyang:will@172.20.0.32:21/helloworld.html
     * @param url
     */
    public SimpleFtpServerInfo(String url) {
        URL tmpUrl = null;
        try {
            tmpUrl = new URL(url);
            this.hostName = tmpUrl.getHost();
            if (tmpUrl.getPort() != -1) {
                this.port = tmpUrl.getPort();
            }
            if (tmpUrl.getUserInfo() != null) {
                String[] userInfo = tmpUrl.getUserInfo().split(":");
                if (userInfo.length >= 2) {
                    this.userName = userInfo[0];
                    this.password = userInfo[1];
                }
                else if (userInfo.length > 0) {
                    this.userName = userInfo[0];
                }
            }
        }
        catch (MalformedURLException e) {

        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerInfo [userName=");
        builder.append(userName);
        builder.append(", password=");
        builder.append(password);
        builder.append(", hostName=");
        builder.append(hostName);
        builder.append(", port=");
        builder.append(port);
        builder.append(", passiveMode=");
        builder.append(passiveMode);
        builder.append(", transBinary=");
        builder.append(transBinary);
        builder.append("]");
        return builder.toString();
    }

}
