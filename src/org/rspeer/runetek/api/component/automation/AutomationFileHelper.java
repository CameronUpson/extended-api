package org.rspeer.runetek.api.component.automation;

import java.io.File;

public class AutomationFileHelper {

    public static File getApiKeyFile() {
        return getFile("cache\\rspeer_me.txt");
    }

    public static File getCurrentVersionFile() {
        return getFile("cache\\current_version.txt");
    }

    public static File getFile(String path) {
        String os = System.getProperty("os.name").toLowerCase();
        StringBuilder sb = new StringBuilder().append(System.getProperty("user.home"));
        if (os.contains("win")) {
            sb.append("\\Documents\\RSPeer\\");
        } else if (os.contains("osx") || os.contains("nix") || os.contains("aix") || os.contains("nux")) {
            sb.append("\\RSPeer\\");
        }
        sb.append(path);
        return new File(sb.toString());
    }
}
