package com.dumbdogdiner.betterwhitelist_bungee.discord;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.commands.GetStatusCommand;
import com.dumbdogdiner.betterwhitelist_bungee.discord.commands.HelpCommand;
import com.dumbdogdiner.betterwhitelist_bungee.discord.commands.UnwhitelistCommand;
import com.dumbdogdiner.betterwhitelist_bungee.discord.commands.WhitelistCommand;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.discord.listeners.GuildEventListener;
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
import java.util.stream.Collectors;

/**
 * Discord bot for whitelisting users from DDD itself.
 */
public class WhitelistBot {

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
    public void init() {
        var builder = new JDABuilder(AccountType.BOT)
            .setToken(PluginConfig.getConfig().getString("discord.token"));

        configureMemory(builder);

        // Register Events
        builder.addEventListeners(
            new ReadyListener(),
            new GuildEventListener(),
            new MessageListener()
        );

        // Register Commands
        addCommand(
            new GetStatusCommand(),
            new WhitelistCommand(),
            new UnwhitelistCommand(),
            new HelpCommand()
        );

        getLogger().info(String.format(
            "[discord] Have %d commands: %s",
            commands.size(),
            commands.values().stream().map(Command::getName).collect(Collectors.joining(", "))
        ));

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
     * @param commandsToAdd
     */
    public static void addCommand(Command... commandsToAdd) {
        for (Command cmd : commandsToAdd) {
            commands.put(cmd.getName(), cmd);
        }
    }
}
