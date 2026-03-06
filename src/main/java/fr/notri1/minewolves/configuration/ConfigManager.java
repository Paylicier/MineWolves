package fr.notri1.minewolves.configuration;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public class ConfigManager {

    private static final Path CONFIG_PATH = Path.of("config.yml");

    private final YamlConfigurationLoader loader;
    private MineWolvesConfig config;

    public ConfigManager() {
        this.loader = YamlConfigurationLoader.builder()
                .path(CONFIG_PATH)
                .build();
    }

    /**
     * Loads the configuration from config.yml.
     * If the file does not exist, it will be created with default values.
     */
    public void load() throws ConfigurateException {
        ConfigurationNode node = loader.load();
        this.config = node.get(MineWolvesConfig.class, new MineWolvesConfig());

        // Save back to create the file with defaults if it doesn't exist
        save();
    }

    /**
     * Saves the current configuration to config.yml.
     */
    public void save() throws ConfigurateException {
        ConfigurationNode node = loader.createNode();
        node.set(MineWolvesConfig.class, this.config);
        loader.save(node);
    }

    public MineWolvesConfig getConfig() {
        return config;
    }
}

