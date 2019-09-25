package org.rspeer.runetek.api.bot_management.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class QuickLaunch {
    private String quickLaunchId;
    private String name;
    private List<Client> clients;
    private String dateAdded;
    private String lastUpdated;

    public String getQuickLaunchId() {
        return quickLaunchId;
    }

    public String getName() {
        return name;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public JsonObject get() {
        final Gson gson = new Gson();
        final JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("dateAdded", dateAdded);
        obj.addProperty("lastUpdated", lastUpdated);
        final JsonArray jsonClients = new JsonArray();
        for (Client client : clients) {
            jsonClients.add(gson.toJsonTree(client));
        }
        obj.add("clients", jsonClients);
        return obj;
    }

    @Override
    public String toString() {
        return "QuickLaunch{" +
                "quickLaunchId='" + quickLaunchId + '\'' +
                ", name='" + name + '\'' +
                ", clients=" + clients.toString() +
                ", dateAdded='" + dateAdded + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }

    public class Client {
        private String rsUsername;
        private String rsPassword;
        private int world;
        private Proxy proxy;
        private Script script;
        private Config config;

        public Client(String rsUsername, String rsPassword, int world, Proxy proxy, Script script, Config config) {
            this.rsUsername = rsUsername;
            this.rsPassword = rsPassword;
            this.world = world;
            this.proxy = proxy;
            this.script = script;
            this.config = config;
        }

        public String getRsUsername() {
            return rsUsername;
        }

        public String getRsPassword() {
            return rsPassword;
        }

        public int getWorld() {
            return world;
        }

        public Proxy getProxy() {
            return proxy;
        }

        public Script getScript() {
            return script;
        }

        public Config getConfig() {
            return config;
        }


        @Override
        public String toString() {
            return "Client{" +
                    "rsUsername='" + rsUsername + '\'' +
                    ", rsPassword='" + rsPassword + '\'' +
                    ", world=" + world +
                    ", proxy=" + proxy.toString() +
                    ", script=" + script.toString() +
                    ", config=" + config.toString() +
                    '}';
        }
    }

    public class Config {
        private boolean lowCpuMode;
        private boolean superLowCpuMode;
        private int engineTickDelay;
        private boolean disableModelRendering;
        private boolean disableSceneRendering;

        public Config(boolean lowCpuMode, boolean superLowCpuMode, int engineTickDelay, boolean disableModelRendering, boolean disableSceneRendering) {
            this.lowCpuMode = lowCpuMode;
            this.superLowCpuMode = superLowCpuMode;
            this.engineTickDelay = engineTickDelay;
            this.disableModelRendering = disableModelRendering;
            this.disableSceneRendering = disableSceneRendering;
        }

        public boolean isLowCpuMode() {
            return lowCpuMode;
        }

        public boolean isSuperLowCpuMode() {
            return superLowCpuMode;
        }

        public int getEngineTickDelay() {
            return engineTickDelay;
        }

        public boolean isDisableModelRendering() {
            return disableModelRendering;
        }

        public boolean isDisableSceneRendering() {
            return disableSceneRendering;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "lowCpuMode=" + lowCpuMode +
                    ", superLowCpuMode=" + superLowCpuMode +
                    ", engineTickDelay=" + engineTickDelay +
                    ", disableModelRendering=" + disableModelRendering +
                    ", disableSceneRendering=" + disableSceneRendering +
                    '}';
        }
    }

    public class Proxy {
        private String proxyId;
        private String date;
        private String userId;
        private String name;
        private String ip;
        private int port;
        private String username;
        private String password;

        public Proxy(String proxyId, String date, String userId, String name, String ip, int port, String username, String password) {
            this.proxyId = proxyId;
            this.date = date;
            this.userId = userId;
            this.name = name;
            this.ip = ip;
            this.port = port;
            this.username = username;
            this.password = password;
        }

        public String getProxyId() {
            return proxyId;
        }

        public String getDate() {
            return date;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "Proxy{" +
                    "proxyId='" + proxyId + '\'' +
                    ", date='" + date + '\'' +
                    ", userId='" + userId + '\'' +
                    ", name='" + name + '\'' +
                    ", ip='" + ip + '\'' +
                    ", port=" + port +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    public class Script {
        private String scriptArgs;
        private String name;
        private String scriptId;
        private boolean isRepoScript;

        public Script(String scriptArgs, String name, String scriptId, boolean isRepoScript) {
            this.scriptArgs = scriptArgs;
            this.name = name;
            this.scriptId = scriptId;
            this.isRepoScript = isRepoScript;
        }

        public String getScriptArgs() {
            return scriptArgs;
        }

        public String getName() {
            return name;
        }

        public String getScriptId() {
            return scriptId;
        }

        public boolean isRepoScript() {
            return isRepoScript;
        }

        @Override
        public String toString() {
            return "Script{" +
                    "scriptArgs='" + scriptArgs + '\'' +
                    ", name='" + name + '\'' +
                    ", scriptId='" + scriptId + '\'' +
                    ", isRepoScript=" + isRepoScript +
                    '}';
        }
    }
}
