package org.rspeer.runetek.api.automation.data;

public class UserInfo {
    private int uid;
    private int gid;
    private String username;
    private String homedir;
    private String shell;

    public UserInfo(int uid, int gid, String username, String homedir, String shell) {
        this.uid = uid;
        this.gid = gid;
        this.username = username;
        this.homedir = homedir;
        this.shell = shell;
    }

    public int getUid() {
        return uid;
    }

    public int getGid() {
        return gid;
    }

    public String getUsername() {
        return username;
    }

    public String getHomedir() {
        return homedir;
    }

    public String getShell() {
        return shell;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid=" + uid +
                ", gid=" + gid +
                ", username='" + username + '\'' +
                ", homedir='" + homedir + '\'' +
                ", shell='" + shell + '\'' +
                '}';
    }
}
