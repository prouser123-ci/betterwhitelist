package com.dumbdogdiner.betterwhitelist_bungee.discord;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.commands.WhitelistCommand;
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

    private static WhitelistBot instance;
    private static JDA jda = null;
    private static HashMap<String, Command> commands = new HashMap<>();

    private WhitelistBot() { }

    public static WhitelistBot getInstance() {
        if (instance == null) {
            instance = new WhitelistBot();
        }
        return instance;
    }

    // Getters
    public static JDA getJda() {
        return jda;
    }

    public static Logger getLogger() {
        return BetterWhitelistBungee.getInstance().getLogger();
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }

    /**
     * Initialize the bot.
     */
    public static void init() {
        var builder = new JDABuilder(AccountType.BOT);
        try {
            configureMemory(builder);

            jda = new JDABuilder()
                .setToken(BetterWhitelistBungee.getInstance().configManager.getConfig().getString("discord.token"))
                .addEventListeners(
                    new ReadyListener(),
                    new GuildBanListener(),
                    new GuildLeaveListener(),
                    new MessageListener()
                ).build();

            addCommand(new WhitelistCommand());
        } catch (LoginException err) {
            getLogger().severe("WhitelistBot threw an error while trying to authenticate with Discord.");
            err.printStackTrace();
        }
    }

    /**
     * Configure flags for the JDABuilder.
     * @param builder
     */
    private static void configureMemory(JDABuilder builder) {
        builder.setDisabledCacheFlags(
                EnumSet.of(CacheFlag.ACTIVITY)
        );
    }

    /**
     * Add commands to the bot.
     * @param commands
     */
    public static void addCommand(Command... commands) {
        for (Command cmd : commands) {
            getCommands().put(cmd.getName(), cmd);
        }
    }
}
