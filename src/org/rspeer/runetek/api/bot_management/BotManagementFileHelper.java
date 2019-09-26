package org.rspeer.runetek.api.bot_management;

import org.rspeer.script.Script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class BotManagementFileHelper {

    public static File getCurrentVersionFile() {
        return getFile("cache" + File.separator + "current_version");
    }

    public static File getFile(String path) {
        return new File(Script.getDataDirectory().getParent().getParent() + File.separator + path);
    }

    public static String getApiKeyOrThrow() throws IOException {
        String message = "Api key not found in rspeer_me.";
        final File file = getFile("cache" + File.separator + "rspeer_me");
        if (!file.exists()) {
            throw new FileNotFoundException(message);
        }
        final String key = Files.lines(file.toPath()).findFirst().orElse(null);
        if(key == null || key.length() == 0) {
            throw new FileNotFoundException(message);
        }
        return key;
    }
}
