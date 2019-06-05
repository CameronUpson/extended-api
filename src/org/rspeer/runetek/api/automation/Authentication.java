package org.rspeer.runetek.api.automation;

import java.io.*;

public class Authentication {

    public static String getApiKey() throws IOException {
        final File apiKeyFile = AutomationFileHelper.getApiKeyFile();
        if (!apiKeyFile.exists())
            throw new FileNotFoundException("No api key file found");

        final BufferedReader br = new BufferedReader(new FileReader(apiKeyFile));
        final String result = br.readLine();
        br.close();
        return result;
    }
}
