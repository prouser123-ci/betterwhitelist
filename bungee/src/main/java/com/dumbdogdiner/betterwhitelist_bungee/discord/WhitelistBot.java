package com.dumbdogdiner.betterwhitelist_bungee.discord;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.commands.GetStatusCommand;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.GuildBanListener;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.GuildLeaveListener;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.MessageListener;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.ReadyListener;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Discord bot for whitelisting users from DDD itself.
 */
public class WhitelistBot extends Thread {

    private static WhitelistBot instance;
    public static WhitelistBot getInstance() {
        if (instance == null) {
            instance = new WhitelistBot();
        }
        return instance;
    }

    private WhitelistBot() { }

    private static JDA jda = null;
    public static JDA getJda() {
        return jda;
    }

    private static HashMap<String, Command> commands = new HashMap<>();
    public static HashMap<String, Command> getCommands() {
        return commands;
    }

    public static Logger getLogger() {
        return BetterWhitelistBungee.getInstance().getLogger();
    }

    /**
     * Initialize the bot.
     */
    @Override
    public void run() {
        var builder = new JDABuilder(AccountType.BOT)
            .setToken(PluginConfig.getConfig().getString("discord.token"));

        configureMemory(builder);

        // Register Events
        builder.addEventListeners(
            new ReadyListener(),
            new GuildBanListener(),
            new GuildLeaveListener(),
            new MessageListener()
        );

        // Register Commands
        addCommand(new GetStatusCommand());

        builder.setActivity(Activity.watching("the cutest fluffs \uD83E\uDDE1"));

        try {
            getLogger().info("[discord] Using access token '" + PluginConfig.getConfig().getString("discord.token") + "'...");
            jda = builder.build();
        } catch (LoginException err) {
            getLogger().severe("[discord] WhitelistBot threw an error while trying to authenticate with Discord.");
            err.printStackTrace();
        }
    }

    /**
     * Configure flags for the JDABuilder. Saves memory :3
     * @param builder
     *
     * TODO: This breaks: "java.lang.ClassNotFoundException: net.dv8tion.jda.api.utils.cache.CacheFlag"
     */

    private static void configureMemory(JDABuilder builder) {
        builder.setDisabledCacheFlags(
                EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
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
