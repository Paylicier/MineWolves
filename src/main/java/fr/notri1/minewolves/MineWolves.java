package fr.notri1.minewolves;

import fr.notri1.minewolves.commands.debug.RoleMenuCommand;
import fr.notri1.minewolves.commands.debug.SeerMenuCommand;
import fr.notri1.minewolves.commands.debug.StartCommand;
import fr.notri1.minewolves.commands.debug.SpecTestCommand;
import fr.notri1.minewolves.configuration.ConfigManager;
import fr.notri1.minewolves.configuration.MineWolvesConfig;
import fr.notri1.minewolves.game.MineWolvesManager;
import fr.notri1.minewolves.listeners.Listeners;
import fr.notri1.minewolves.pack.PackGenerator;
import fr.notri1.minewolves.pack.WebServer;
import net.hollowcube.minestom.extensions.ExtensionBootstrap;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.spongepowered.configurate.ConfigurateException;
import fr.notri1.minewolves.Version;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Objects;

public class MineWolves {

    public static InstanceContainer instanceContainer;
    public static MineWolvesManager mineWolvesManager;
    public static ConfigManager configManager;
    public static MineWolvesConfig config;

    public static void main(String[] args) {
        // Load configuration
        configManager = new ConfigManager();
        try {
            configManager.load();
        } catch (ConfigurateException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        config = configManager.getConfig();

        // Initialization
        ExtensionBootstrap minecraftServer = ExtensionBootstrap.init();

        String authConfig = config.getServer().getAuth();

        // Auth = none -> cracked, bungee -> bungeecord, else -> velocity w/ secret (from string)
        Auth auth = switch (authConfig) {
            case "none" -> new Auth.Offline();
            case "bungee" -> new Auth.Bungee();
            default -> new Auth.Velocity(authConfig);
        };

        MinecraftServer.init(auth);

        MinecraftServer.setBrandName(config.getServer().getBrandName() + "@" + Version.COMMIT_SHA + " " + MinecraftServer.VERSION_NAME);

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instanceContainer = instanceManager.createInstanceContainer();

        // No time for u
        instanceContainer.setTimeRate(0);

        // Load map
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            instanceContainer.setChunkLoader(new PolarLoader(Objects.requireNonNull(loader.getResourceAsStream("map.polar"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Listeners.init();

        MinecraftServer.getCommandManager().register(new StartCommand());
        MinecraftServer.getCommandManager().register(new SpecTestCommand());
        MinecraftServer.getCommandManager().register(new RoleMenuCommand());
        MinecraftServer.getCommandManager().register(new SeerMenuCommand());

        mineWolvesManager = new MineWolvesManager();

        // Generate resource pack and start web server
        try {
            PackGenerator.generate();
            WebServer webServer = new WebServer();
            webServer.start();
        } catch (Exception e) {
            System.err.println("Failed to start resource pack web server: " + e.getMessage());
            e.printStackTrace();
        }


        // Cloudnet things, fallback on config
        String host = System.getProperty("service.bind.host", config.getServer().getHost());
        int port = Integer.getInteger("service.bind.port", config.getServer().getPort());

        // Start the server
        minecraftServer.start(host, port);
    }
}