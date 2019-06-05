package org.rspeer.runetek.api.automation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        Files.copy(is, Paths.get(AutomationFileHelper.getFile("cache" + File.separator + "rspeer.jar").toURI()));
        is.close();

        saveVersionFile(getCurrentJarVersion());
        return true;
    }

    private static void saveVersionFile(String version) throws IOException {
        final File file = AutomationFileHelper.getCurrentVersionFile();
        Files.write(file.toPath(), version.getBytes());
    }

    public static boolean shouldDownload() throws IOException {
        final File file = AutomationFileHelper.getCurrentVersionFile();
        if (!file.exists())
            return true;
        final String currentJarVersion = getCurrentJarVersion();
        final String localJarVersion = Files.lines(file.toPath()).findFirst().orElse("");
        return !currentJarVersion.isEmpty() && !currentJarVersion.equals(localJarVersion);
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
