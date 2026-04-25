package fr.notri1.minewolves.configuration;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.net.URI;
import java.util.List;

@ConfigSerializable
public class MineWolvesConfig {

    private ServerConfig server = new ServerConfig();
    private GameConfig game = new GameConfig();
    private WebConfig web = new WebConfig();

    public ServerConfig getServer() {
        return server;
    }

    public GameConfig getGame() {
        return game;
    }

    public WebConfig getWeb() {
        return web;
    }

    @ConfigSerializable
    public static class ServerConfig {
        private String host = "0.0.0.0";
        private int port = 25565;

        private String brandName = "MineWolves";

        private String auth = "none";

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getBrandName() {
            return brandName;
        }

        public String getAuth() {
            return auth;
        }
    }

    @ConfigSerializable
    public static class WebConfig {
        private URI url = URI.create("http://localhost:8080/pack.zip");
        private int port = 8080;

        public @NotNull URI getUrl() {
            return url;
        }

        public int getPort() {
            return port;
        }
    }

    @ConfigSerializable
    public static class GameConfig {
        private int minPlayers = 4;
        private int maxPlayers = 18;
        private int countdownSeconds = 5;
        private List<List<Float>> sitPoints = List.of(
                List.of(18f, -47f, 41f),
                List.of(20f, -47f, 31f),
                List.of(24f, -47f, 37f),
                List.of(22f, -47f, 40f),
                List.of(24f, -47f, 35f),
                List.of(19f, -47f, 41f),
                List.of(24f, -47f, 38f),
                List.of(16f, -47f, 38f),
                List.of(18f, -47.5f, 37f),
                List.of(22f, -47.5f, 35f),
                List.of(21f, -47.5f, 38f),
                List.of(22f, -47f, 32f),
                List.of(15f, -47f, 36f)
                //todo: add more (-> 18)
        );

        private List<Float> menuLocation = List.of(1f, -40f, 16f, 90f, 0f);

        public int getMinPlayers() {
            return minPlayers;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public int getCountdownSeconds() {
            return countdownSeconds;
        }

        public List<List<Float>> getSitPoints() {
            return sitPoints;
        }

        public List<Float> getMenuLocation() {
            return menuLocation;
        }
    }
}

