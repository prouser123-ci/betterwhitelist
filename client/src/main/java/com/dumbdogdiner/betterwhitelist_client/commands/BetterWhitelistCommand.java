package com.dumbdogdiner.betterwhitelist_client.commands;

import com.dumbdogdiner.betterwhitelist_client.BetterWhitelistClientPlugin;
import com.dumbdogdiner.betterwhitelist_client.utils.Permissions;
import com.dumbdogdiner.betterwhitelist_client.utils.URLRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BetterWhitelistCommand implements CommandExecutor {
    private FileConfiguration plConf = BetterWhitelistClientPlugin.getPlugin().getConfig();

    private String[][] helpMessages = {
            {"/btw reload", "Reloads the plugin and its configuration."},
            {"/btw help", "Display this help message :3"},
            {"/btw whois <user>", "Fetch cross-server information about a user."},
            {"/btw list [page=0]>", "Display users whitelisted across the entire network."},
            {"/btw add <user>", "Add a player to the whitelist."},
            {"/btw remove <user>", "Remove a player from the whitelist."}
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.checkPermission(sender)) {
            return true;
        }

        String subCommand;
        String[] commandArgs;

        if (args.length > 0) {
            subCommand = args[0];
            commandArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
        } else {
            return help(sender, args);
        }

        // Handle commands
        switch(subCommand) {
            case "list":
                return list(sender, commandArgs);

            case "reload":
                return reload(sender, commandArgs);

            default:
                return help(sender, commandArgs);
        }
    }


    // SUB COMMANDS

    /**
     * Send help message to chat.
     * @param sender
     * @param args
     * @return
     */
    private boolean help(CommandSender sender, String[] args) {
        sender.sendMessage(
                String.format("%s %sBetterWhiteList v%s",
                        ChatColor.AQUA,
                        BetterWhitelistClientPlugin.getPlugin().getDescription().getVersion()
                )
        );

        for (String[] msg : helpMessages) {
            sender.sendMessage(String.format("%s%s%s - %s", ChatColor.YELLOW, msg[0], ChatColor.WHITE, msg[1]));
        }
        return true;
    }

    /**
     * Reload the plugin and its configuration.
     * @param sender
     * @param args
     * @return
     */
    private boolean reload(CommandSender sender, String[] args) {
        if (!Permissions.checkPermission(sender, "reload")) {
            return true;
        }

        BetterWhitelistClientPlugin.getPlugin().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        return true;
    }

    /**
     * List whitelisted players.
     * @param sender
     * @param args
     * @return
     */
    private boolean list(CommandSender sender, String[] args) {
        var names = Bukkit.getWhitelistedPlayers().stream().map(p -> p.getName()).collect(Collectors.toList());

        sender.sendMessage("There are currently " + names.size() + " players whitelisted: "
                + String.join(", ", names));

        return true;
    }

    /**
     * Remove a specified player from the whitelist.
     * @param sender
     * @param args
     * @return
     */
    private boolean remove(CommandSender sender, String[] args) {
        if (args[0] == null) {
            sender.sendMessage(ChatColor.RED + "Please specify a player!");
            return true;
        }

        int response = 0;
        try {
            response = URLRequest.checkUsername(args[1]);
            Bukkit.getLogger().log(Level.FINE, "Request to Mojang API returned response code " + response);
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }

        if (response != 200) {
            sender.sendMessage(ChatColor.RED + "if (!banSyncEnabled) {\n" +
                    "            plugin.getLogger().info(String.format(\"Not checking if '%u' should be banned - banSyncEnabled=false\", e.getPlayer().getUniqueId().toString()));\n" +
                    "            return;\n" +
                    "        }This player does not exist!");
            return true;
        }


        OfflinePlayer p = Bukkit.getPlayer(args[0]);

        if (!p.isWhitelisted()) {
            sender.sendMessage(ChatColor.RED + "This player is not whitelisted!");
            return true;
        }

        p.setWhitelisted(false);

        // TODO: BungeeCord messaging.

        sender.sendMessage(ChatColor.GREEN + "Removed " + p.getName() + " from the whitelist!");
        if (p.isOnline()) {
            Bukkit.getPlayer(p.getUniqueId()).kickPlayer("You've been removed from the whitelist");
        }

        return true;
    }

}
