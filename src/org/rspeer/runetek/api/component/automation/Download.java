package org.rspeer.runetek.api.component.automation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;

public class Download {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    private static boolean downloadNewJar() throws IOException {
        if (!shouldDownload())
            return true;

        final String apiKey = Authentication.getApiKey();
        if (apiKey.isEmpty())
            return false;

        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/bot/currentJar")
                .header("ApiClient", apiKey)
                .get()
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();
        if (!response.isSuccessful())
            return false;

        final ResponseBody body = response.body();
        if (body == null)
            return false;

        final InputStream is = body.byteStream();

        final BufferedInputStream input = new BufferedInputStream(is);
        final OutputStream output = new FileOutputStream(AutomationFileHelper.getFile("cache\\rspeer.jar"));

        byte[] data = new byte[1024];
        int count;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
        }
        output.flush();
        output.close();
        input.close();
        return true;
    }

    public static boolean shouldDownload() throws IOException {
        final File file = AutomationFileHelper.getCurrentVersionFile();
        if (!file.exists())
            return true;
        final BufferedReader br = new BufferedReader(new FileReader(file));
        final String currentJarVersion = getCurrentJarVersion();
        return !currentJarVersion.isEmpty() && !currentJarVersion.equals(br.readLine());
    }

    public static String getCurrentJarVersion() throws IOException {
        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/bot/currentVersion")
                .get()
                .build();
        final Response response = HTTP_CLIENT.newCall(request).execute();
        if (!response.isSuccessful())
            return "";

        final ResponseBody body = response.body();
        if (body == null)
            return "";

        final Gson gson = new Gson().newBuilder().create();
        return gson.fromJson(body.string(), JsonObject.class)
                .get("version")
                .getAsString();
    }
}
