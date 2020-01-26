package com.dumbdogdiner.betterwhitelist.listeners;

import com.dumbdogdiner.betterwhitelist.BetterWhitelist;
import com.dumbdogdiner.betterwhitelist.discord.DiscordBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerBan(PlayerQuitEvent e) throws IOException {
        if (!banSyncEnabled) {
            return;
        }
        Player p = e.getPlayer();

        if (!p.isBanned()) {
            return;
        }

        if (BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("file")) {
            Reader reader = Files
                    .newBufferedReader(Paths.get(BetterWhitelist.getPlugin().getDataFolder() + "/userdata.csv"));
            List<String[]> userData = BetterWhitelist.getUserData(reader);

            for (int i = 0; i < userData.size(); i++) {
                String[] current = userData.get(i);

                if (current[0].equals(p.getUniqueId().toString())) {

                    String userID = current[1];
                    Guild guild = DiscordBot.getJda().getGuildById(guildID);
                    Member member = guild.getMemberById(userID);
                    guild.ban(member, 0).queue();
                    break;
                }

            }
        } else if (BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("sql")) {
            String discordID = MySQLRequest.getDiscordIDFromMinecraft(p.getUniqueId().toString());
            if (discordID != null) {
                Guild guild = DiscordBot.getJda().getGuildById(guildID);
                Member member = guild.getMemberById(discordID);
                guild.ban(member, 0).queue();
                MySQLRequest.removeEntry(discordID);
            }
        }

    }
}
