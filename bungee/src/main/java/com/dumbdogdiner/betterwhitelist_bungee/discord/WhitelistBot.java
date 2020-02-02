package com.dumbdogdiner.betterwhitelist_bungee.discord;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.GuildBanListener;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.GuildLeaveListener;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.MessageListener;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.ReadyListener;
import com.dumbdogdiner.betterwhitelist_bungee.utils.ConfigManager;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;;


import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Discord bot for whitelisting users from DDD itself.
 */
public class WhitelistBot {
    public JDA jda;
    public BetterWhitelistBungee plugin;
    public Logger logger = plugin.getLogger();
    public ConfigManager configManager = plugin.configManager;

    public Map<String, Command> commands = new HashMap<>();

    public WhitelistBot(BetterWhitelistBungee plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the bot.
     */
    public void init() {
        var builder = new JDABuilder(AccountType.BOT);
        try {
            configureMemory(builder);
            jda = new JDABuilder()
                .setToken(plugin.configManager.getConfig().getString("discord.token"))
                .addEventListeners(
                    new ReadyListener(this),
                    new GuildBanListener(this),
                    new GuildLeaveListener(this),
                    new MessageListener(this)
                ).build();

            this.addCommand();
        } catch (LoginException err) {
            logger.severe("WhitelistBot threw an error while trying to authenticate with Discord.");
            err.printStackTrace();
        }
    }

    /**
     * Configure flags for the JDABuilder.
     * @param builder
     */
    private void configureMemory(JDABuilder builder) {
        builder.setDisabledCacheFlags(
                EnumSet.of(CacheFlag.ACTIVITY)
        );
    }

    /**
     * Add commands to the bot.
     * @param commands
     */
    public void addCommand(Command... commands) {
        for (Command cmd : commands) {
            this.commands.put(cmd.getName(), cmd);
        }
    }
}
