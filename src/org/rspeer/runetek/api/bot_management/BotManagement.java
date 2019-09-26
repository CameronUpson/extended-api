package org.rspeer.runetek.api.bot_management;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.rspeer.runetek.api.bot_management.data.LaunchedClient;
import org.rspeer.runetek.api.bot_management.data.Launcher;
import org.rspeer.runetek.api.bot_management.data.QuickLaunch;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class BotManagement {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final String DEFAULT_JVM_ARGS = "-Xmx768m -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -Xss2m";

    public static boolean startDefaultClient(int pcIndex) throws IOException {
        return startClient(pcIndex,
                null,
                DEFAULT_JVM_ARGS,
                10,
                null,
                1
        );
    }

    public static boolean startClient(int pcIndex, int sleep, String proxy, int count) throws IOException {
        return startClient(pcIndex, "", sleep, proxy, count);
    }

    public static boolean startClient(int pcIndex, int sleep, int count) throws IOException {
        return startClient(pcIndex, "", sleep, "", count);
    }

    public static boolean startClient(int pcIndex, String qs, int sleep, String proxy, int count) throws IOException {
        return startClient(
                pcIndex,
                qs,
                DEFAULT_JVM_ARGS,
                sleep,
                proxy,
                count
        );
    }

    public static boolean startClient(int pcIndex, String qs, String jvmArgs, int sleep, String proxy, int count) throws IOException {
        final String apiKey = BotManagementFileHelper.getApiKeyOrThrow();

        List<Launcher> launchers = getLaunchers();
        final Headers headers = new Headers.Builder()
                .add("ApiClient", apiKey)
                .add("Content-Type", "application/json")
                .build();

        String body = "{\"payload\":" +
                "{" +
                "\"type\":\"start:client\"," +
                "\"session\":\"" + apiKey + "\"," +
                "\"qs\":" + (qs == null ? "null" : qs) + "," +
                "\"jvmArgs\":\"" + jvmArgs + "\"," +
                "\"sleep\":" + sleep + "," +
                "\"proxy\":" + proxy + "" +
                (count > 0 ? ",\"count\":" + count : "") +
                "}," +
                "\"socket\":\"" + launchers.get(pcIndex).getSocketAddress() + "\"" +
                "}";

        final RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                body
        );

        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/botLauncher/send")
                .headers(headers)
                .post(requestBody)
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();
        return response.isSuccessful();
    }

    public static boolean addProxy(String name, String ip, String port, String username, String password) throws Exception {
        final String apiKey = BotManagementFileHelper.getApiKeyOrThrow();

        final Headers headers = new Headers.Builder()
                .add("ApiClient", apiKey)
                .add("Content-Type", "application/json")
                .build();

        final String body =
                "{" +
                        "\"Name\":\"" + name + "\"," +
                        "\"Ip\":\"" + ip + "\"," +
                        "\"Port\":\"" + port + "\"," +
                        "\"Username\":\"" + username + "\"," +
                        "\"Password\":\"" + password + "\"" +
                        "}";

        final RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                body
        );

        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/botLauncher/saveProxy")
                .headers(headers)
                .post(requestBody)
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();
        return response.isSuccessful();
    }

    public static List<QuickLaunch> getQuickLaunchers() throws IOException {
        final String apiKey = BotManagementFileHelper.getApiKeyOrThrow();

        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/botLauncher/getQuickLaunch")
                .header("ApiClient", apiKey)
                .get()
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();

        if (!response.isSuccessful())
            return Collections.emptyList();

        final ResponseBody body = response.body();
        if (body == null)
            return Collections.emptyList();

        final Gson gson = new Gson().newBuilder().create();
        final Type clientType = new TypeToken<List<QuickLaunch>>() {
        }.getType();

        return gson.fromJson(body.string(), clientType);
    }

    public static List<LaunchedClient> getRunningClients() throws IOException {
        final String apiKey = BotManagementFileHelper.getApiKeyOrThrow();

        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/botLauncher/connectedClients")
                .header("ApiClient", apiKey)
                .get()
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();

        if (!response.isSuccessful())
            return Collections.emptyList();

        final ResponseBody body = response.body();
        if (body == null)
            return Collections.emptyList();

        final Gson gson = new Gson().newBuilder().create();
        final Type type = new TypeToken<List<LaunchedClient>>() {
        }.getType();
        return gson.fromJson(body.string(), type);
    }

    public static List<Launcher> getLaunchers() throws IOException {
        final String apiKey = BotManagementFileHelper.getApiKeyOrThrow();

        final Request request = new Request.Builder()
                .url("https://services.rspeer.org/api/botLauncher/connected")
                .header("ApiClient", apiKey)
                .get()
                .build();

        final Response response = HTTP_CLIENT.newCall(request).execute();

        if (!response.isSuccessful())
            return Collections.emptyList();

        final ResponseBody body = response.body();
        if (body == null)
            return Collections.emptyList();

        final Gson gson = new Gson().newBuilder().create();
        final JsonObject jsonObject = gson.fromJson(body.string(), JsonObject.class);
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        final List<Launcher> launchers = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : entries) {
            Launcher launcher = gson.fromJson(entry.getValue().getAsJsonObject(), Launcher.class);
            launcher.setSocketAddress(entry.getKey());
            launchers.add(launcher);
        }
        return launchers;
    }
}
