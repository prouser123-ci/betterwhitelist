package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PluginConfig {

    private PluginConfig() { }


    private static Configuration config;

    /**
     * Fetch and cache the configuration file for the plugin.
     */
    public static Configuration getConfig() {
        if (config != null) {
            return config;
        }
        return loadConfig();
    }

    /**
     * Return the prefix used by the Discord bot.
     * @return
     */
    public static String getPrefix() {
        var prefix = getConfig().getString("discord.prefix");
        return prefix == null ? "-" : prefix;
    }
    /**
     * Save the current cached config to disk.
     * @return
     */
    public static boolean saveConfig() {
        BetterWhitelistBungee.getInstance().getLogger().info("Saving configuration to disk...");
        return writeConfig(config);
    }

    /**
     * Write the provided configuration to 'config.yml'.
     * @param configuration
     */
    private static boolean writeConfig(Configuration configuration) {
        var file =  new File(BetterWhitelistBungee.getInstance().getDataFolder(), "config.yml");

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
            return true;
        } catch (IOException err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * Use the internal 'config.yml' file as configuration.
     * @return
     */
    private static Configuration useDefaultConfig() {
        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(BetterWhitelistBungee.getInstance().getResourceAsStream("config.yml"));
        return config = configuration;
    }

    /**
     * Fetch the 'config.yml' stored in the plugin data folder.
     * @return
     *
     * TODO: Optimize this - repeats itself too much.
     */
    private static Configuration loadConfig() {

        var plugin = BetterWhitelistBungee.getInstance();
        var file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            plugin.getLogger().warning("Plugin configuration 'config.yml' does not exist - using defaults...");
            Configuration defaultConfig = useDefaultConfig();

            writeConfig(defaultConfig);
            return defaultConfig;
        }

        try {
            Configuration savedConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            config = savedConfig;
            plugin.getLogger().info("Loaded configuration.");
            return config;
        } catch(IOException err) {
            plugin.getLogger().warning("Failed to load 'config.yml' - falling back to internal resources.");
            err.printStackTrace();
            return config = useDefaultConfig();
        }
    }
}
