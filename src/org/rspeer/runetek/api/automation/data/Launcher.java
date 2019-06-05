package org.rspeer.runetek.api.automation.data;

import java.util.Arrays;

public class Launcher {
    private String socketAddress;
    private String host;
    private String platform;
    private String type;
    private UserInfo userInfo;
    private String ip;
    private String linkKey;
    private String[] errors;
    private String[] messages;

    public Launcher(String host, String platform, String type, UserInfo userInfo, String ip, String linkKey, String[] errors, String[] messages) {
        this.host = host;
        this.platform = platform;
        this.type = type;
        this.userInfo = userInfo;
        this.ip = ip;
        this.linkKey = linkKey;
        this.errors = errors;
        this.messages = messages;
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(String socketAddress) {
        this.socketAddress = socketAddress;
    }

    public String getHost() {
        return host;
    }

    public String getPlatform() {
        return platform;
    }

    public String getType() {
        return type;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getIp() {
        return ip;
    }

    public String getLinkKey() {
        return linkKey;
    }

    public String[] getErrors() {
        return errors;
    }

    public String[] getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "Launcher{" +
                "socketAddress='" + socketAddress + '\'' +
                ", host='" + host + '\'' +
                ", platform='" + platform + '\'' +
                ", type='" + type + '\'' +
                ", userInfo=" + userInfo.toString() +
                ", ip='" + ip + '\'' +
                ", linkKey='" + linkKey + '\'' +
                ", errors=" + Arrays.toString(errors) +
                ", messages=" + Arrays.toString(messages) +
                '}';
    }
}
