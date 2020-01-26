package com.dumbdogdiner.betterwhitelist.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

import com.dumbdogdiner.betterwhitelist.listeners.MySQLRequest;

public class GuildLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
        Member m = e.getMember();

        if (e.getUser().isBot())
            return;

        String pUUID = MySQLRequest.getMinecraftFromDiscordID(m.getId());
        if (pUUID != null) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(pUUID));
            p.setWhitelisted(false);
            MySQLRequest.removeEntry(m.getId());
        }
    }
}
