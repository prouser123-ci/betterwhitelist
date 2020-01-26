package com.dumbdogdiner.betterwhitelist.discord;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import com.dumbdogdiner.betterwhitelist.BetterWhitelist;
import com.dumbdogdiner.betterwhitelist.listeners.MySQLRequest;

public class GuildBanListener extends ListenerAdapter {

    private FileConfiguration plConf = BetterWhitelist.getPlugin().getConfig();
    private boolean banSyncEnabled = plConf.getBoolean("enableBanSync");

    @SneakyThrows
    @Override
    public void onGuildBan(GuildBanEvent e) {

        if (banSyncEnabled) {
            User user = e.getUser();
            if (BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("file")) {
                Reader reader = null;
                List<String[]> userData = null;

                reader = Files
                        .newBufferedReader(Paths.get(BetterWhitelist.getPlugin().getDataFolder() + "/userdata.csv"));
                userData = BetterWhitelist.getUserData(reader);

                for (int i = 0; i < userData.size(); i++) {
                    String[] current = userData.get(i);

                    if (current[1].equals(user.getId())) {

                        OfflinePlayer player = Bukkit.getOfflinePlayer(current[0]);
                        Bukkit.getPlayer(player.getUniqueId()).kickPlayer("You've been banned on Discord.");
                        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "You've been banned from Discord",
                                null, "console");
                        break;
                    }

                }
            } else if (BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("sql")) {
                UUID pUUID = UUID.fromString(MySQLRequest.getMinecraftFromDiscordID(user.getId()));
                if (pUUID != null) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(pUUID);
                    if (p.isOnline()) {
                        Bukkit.getPlayer(pUUID).kickPlayer("You've been banned on Discord.");
                    }
                    Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), "You've been banned from Discord", null,
                            "console");
                    MySQLRequest.removeEntry(user.getId());
                }
            }

        }

    }
}
