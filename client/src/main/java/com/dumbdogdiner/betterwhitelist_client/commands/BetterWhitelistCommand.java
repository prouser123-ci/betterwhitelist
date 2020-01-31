package com.dumbdogdiner.betterwhitelist_client.commands;

import com.dumbdogdiner.betterwhitelist_client.BetterWhitelistClientPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BetterWhitelistCommand implements CommandExecutor {
    private FileConfiguration plConf = BetterWhitelistClientPlugin.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("betterwhitelist.command")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GOLD + " " + ChatColor.BOLD + "BetterWhitelist "
                        + BetterWhitelist.getPlugin().getDescription().getVersion() + ChatColor.GREEN
                        + " developed by jaquewolfee");
                sender.sendMessage(ChatColor.GOLD + "/betterwhitelist reload " + ChatColor.GREEN
                        + "- Reloads the config file." + ChatColor.GOLD + "\n/betterwhitelist whois <Minecraft User> "
                        + ChatColor.GREEN + "- Displays information about the connected Discord account.");
            } else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.GOLD + "/betterwhitelist reload " + ChatColor.GREEN
                        + "- Reloads the config file." + ChatColor.GOLD + "\n/betterwhitelist whois <Minecraft User> "
                        + ChatColor.GREEN + "- Displays information about the connected Discord account.");
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args[1] == null) {
                    sender.sendMessage(ChatColor.RED + "Please specify a player!");
                } else {
                    int response = 0;
                    try {
                        response = URLRequest.checkUsername(args[1]);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    if (response == 200 && response != 204) {
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);

                        if (p.isWhitelisted()) {
                            p.setWhitelisted(false);
                            String memberID = MySQLRequest.getDiscordIDFromMinecraft(p.getUniqueId().toString());
                            MySQLRequest.removeEntry(memberID);

                            Guild g = BetterWhitelist.getJda()
                                    .getGuildById(BetterWhitelist.getPlugin().getConfig().getString("discord.guildid"));
                            Member m = g.getMemberById(memberID);

                            if (m.getRoles().contains(g.getRoleById(Long.parseLong(
                                    BetterWhitelist.getPlugin().getConfig().getString("discord.giveRole.roleid"))))) {
                                g.removeRoleFromMember(m, g.getRoleById(Long.parseLong(
                                        BetterWhitelist.getPlugin().getConfig().getString("discord.giveRole.roleid"))))
                                        .queue();
                            }
                            sender.sendMessage(ChatColor.GREEN + "Removed " + p.getName() + " from the whitelist!");
                            if (p.isOnline()) {
                                Bukkit.getPlayer(p.getUniqueId()).kickPlayer("You've been removed from the whitelist");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "This player is not whitelisted!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "This player does not exist!");
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                int count = 0;
                String[] whitelistedPlayers = new String[Bukkit.getWhitelistedPlayers().size()];
                for (OfflinePlayer pW : Bukkit.getWhitelistedPlayers()) {
                    if (count != Bukkit.getWhitelistedPlayers().size()) {
                        whitelistedPlayers[count++] = pW.getName();
                    } else {
                        break;
                    }
                }

                sender.sendMessage("There are currently " + count + " players whitelisted: "
                        + String.join(", ", whitelistedPlayers));
            } else if (args[0].equalsIgnoreCase("whois")) {
                if (sender.hasPermission("betterwhitelist.command.whois")) {
                    if (BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("file")) {
                        Reader reader = Files.newBufferedReader(
                                Paths.get(BetterWhitelist.getPlugin().getDataFolder() + "/userdata.csv"));
                        List<String[]> userData = BetterWhitelist.getUserData(reader);
                        boolean userFound = false;

                        for (int i = 0; i < userData.size(); i++) {
                            String[] current = userData.get(i);
                            String pUUID = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId().toString();
                            if (current[0].equals(pUUID)) {
                                Guild guild = BetterWhitelist.getJda().getGuildById(
                                        BetterWhitelist.getPlugin().getConfig().getString("discord.guildid"));
                                Member member = guild.getMemberById(current[1]);
                                userFound = true;
                                sender.sendMessage(ChatColor.DARK_GREEN + "========================================\n"
                                        + ChatColor.GREEN + "Results of " + ChatColor.GOLD
                                        + Bukkit.getServer().getOfflinePlayer(pUUID).getName() + ChatColor.GREEN
                                        + "\nDiscord Username " + ChatColor.GOLD + member.getUser().getAsTag()
                                        + ChatColor.GREEN + "\nAccount created on " + ChatColor.GOLD
                                        + member.getUser().getTimeCreated() + ChatColor.GREEN + "\nUser ID "
                                        + ChatColor.GOLD + member.getUser().getId() + ChatColor.GREEN
                                        + "\nHighest Role " + ChatColor.GOLD + member.getRoles().get(0)
                                        + ChatColor.DARK_GREEN + "========================================");
                                break;
                            }
                        }

                        if (!userFound) {
                            sender.sendMessage(ChatColor.RED + "Could not find user.");
                        }
                    } else if (BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("sql")) {
                        String pUUID = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId().toString();
                        if (MySQLRequest.getDiscordIDFromMinecraft(pUUID) != null) {
                            Guild guild = BetterWhitelist.getJda()
                                    .getGuildById(BetterWhitelist.getPlugin().getConfig().getString("discord.guildid"));
                            Member member = guild.getMemberById(MySQLRequest.getDiscordIDFromMinecraft(pUUID));
                            String highestRole = "none";
                            if (!member.getRoles().isEmpty()) {
                                highestRole = member.getRoles().get(0).getName();
                            }
                            sender.sendMessage(ChatColor.DARK_GREEN + "========================================\n"
                                    + ChatColor.GREEN + "Results of " + ChatColor.GOLD
                                    + Bukkit.getServer().getOfflinePlayer(pUUID).getName() + ChatColor.GREEN
                                    + "\nDiscord Username " + ChatColor.GOLD + member.getUser().getAsTag()
                                    + ChatColor.GREEN + "\nAccount created on " + ChatColor.GOLD
                                    + member.getUser().getTimeCreated() + ChatColor.GREEN + "\nUser ID "
                                    + ChatColor.GOLD + member.getUser().getId() + ChatColor.GREEN + "\nHighest Role "
                                    + ChatColor.GOLD + highestRole + ChatColor.DARK_GREEN
                                    + "========================================");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Could not find user");
                        }
                    }

                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have the required permissions to do this!");
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("betterwhitelist.command.reload")) {
                    BetterWhitelist.getPlugin().reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have the required permissions to do this!");
                }

            } else if (args[0].equalsIgnoreCase("enable")) {
                if (!BetterWhitelist.getPlugin().getConfig().getBoolean("discord.enableAutoWhitelisting")) {
                    sender.sendMessage(ChatColor.GREEN + "Auto-Whitelisting is now enabled!");
                    BetterWhitelist.getPlugin().getConfig().set("discord.enableAutoWhitelisting", true);
                    BetterWhitelist.getPlugin().saveConfig();
                } else {
                    sender.sendMessage(ChatColor.RED + "Auto-Whitelisting is already enabled!");
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (BetterWhitelist.getPlugin().getConfig().getBoolean("discord.enableAutoWhitelisting")) {
                    sender.sendMessage(
                            ChatColor.GREEN + "Auto-Whitelisting is now disabled!\n" + ChatColor.DARK_RED + "WARNING: "
                                    + ChatColor.RED + "Manually added users won't be affected by synced banning!!!");
                    BetterWhitelist.getPlugin().getConfig().set("discord.enableAutoWhitelisting", false);
                    BetterWhitelist.getPlugin().saveConfig();
                } else {
                    sender.sendMessage(ChatColor.RED + "Auto-Whitelisting is already disabled!");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have the required permissions to do this!");
        }

        return true;
    }
}
