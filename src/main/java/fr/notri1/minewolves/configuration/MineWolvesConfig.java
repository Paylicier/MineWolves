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

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getBrandName() {
            return brandName;
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
        private int minPlayers = 3;
        private int maxPlayers = 18;
        private int countdownSeconds = 5;
        private List<List<Integer>> sitPoints = List.of(
                List.of(18, -47, 41),
                List.of(20, -47, 31),
                List.of(24, -47, 37),
                List.of(22, -47, 40),
                List.of(24, -47, 35),
                List.of(19, -47, 41),
                List.of(24, -47, 38),
                List.of(16, -47, 38),
                List.of(15, -47, 38),
                List.of(18, -47.5, 37),
                List.of(22, -47.5, 35),
                List.of(21, -47.5, 48) //todo: add more (-> 18)
        );


        public int getMinPlayers() {
            return minPlayers;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public int getCountdownSeconds() {
            return countdownSeconds;
        }

        public List<List<Integer>> getSitPoints() {
            return sitPoints;
        }
    }
}

