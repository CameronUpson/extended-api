package org.rspeer.runetek.api.automation;

import org.rspeer.script.Script;

import java.io.File;

public class AutomationFileHelper {

    public static File getApiKeyFile() {
        return getFile("cache" + File.separator + "rspeer_me.txt");
    }

    public static File getCurrentVersionFile() {
        return getFile("cache" + File.separator + "current_version.txt");
    }

    public static File getFile(String path) {
        return new File(Script.getDataDirectory().getParent().getParent() + File.separator + path);
    }
}
