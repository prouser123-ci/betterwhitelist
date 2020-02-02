package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    public Configuration config;
    public BetterWhitelistBungee plugin;

    public ConfigManager(BetterWhitelistBungee plugin) {
        this.plugin = plugin;
        this.fetchConfig();
    }

    /**
     * Fetch and cache the configuration file for the plugin.
     */
    public Configuration getConfig() {
        if (config != null) {
            return config;
        }

        return fetchConfig();
    }

    /**
     * Use the internal 'config.yml' file as configuration.
     * @return
     */
    private Configuration useDefaultConfig() {
        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(plugin.getResourceAsStream("config.yml"));
        return config = configuration;
    }

    /**
     * Write the provided configuration to 'config.yml'.
     * @param configuration
     */
    private boolean writeConfig(Configuration configuration) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(plugin.getDataFolder(), "config.yml"));
            return true;
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * Fetch the 'config.yml' stored in the plugin data folder.
     * @return
     */
    private Configuration fetchConfig() {
        File file;
        try {
            file = new File(plugin.getDataFolder(), "config.yml");
        } catch(NullPointerException err) {
            plugin.getLogger().info("Failed to load 'config.yml' - falling back to internal resources.");
            err.printStackTrace();

            Configuration defaultConfig = useDefaultConfig();
            writeConfig(defaultConfig);
            return defaultConfig;
        }

        if (!file.exists()) {
            plugin.getLogger().info("Plugin configuration 'config.yml' does not exist - using defaults...");
            Configuration defaultConfig = useDefaultConfig();
            writeConfig(defaultConfig);
            return defaultConfig;
        }

        try {
            Configuration savedConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            config = savedConfig;
            return config;
        } catch(IOException err) {
            plugin.getLogger().info("Failed to load 'config.yml' - falling back to internal resources.");
            err.printStackTrace();

            Configuration defaultConfig = useDefaultConfig();
            writeConfig(defaultConfig);
            return defaultConfig;
        }
    }
}
