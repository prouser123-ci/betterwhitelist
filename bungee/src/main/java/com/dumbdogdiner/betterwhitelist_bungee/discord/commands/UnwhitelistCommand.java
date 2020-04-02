package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.discord.utils.RoleUtil;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQL;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class UnwhitelistCommand extends Command {

    public UnwhitelistCommand() {
        this.name = "unwhitelist";
        this.description = "Remove yourself from the whitelist of the Minecraft server.";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        e.getChannel().sendTyping().queue();

        var target = e.getMember();

        if (e.getMessage().getMentionedMembers().size() > 0) {
            var member = e.getMessage().getMentionedMembers().get(0);
            if (!Objects.requireNonNull(e.getMember()).canInteract(member)) {
                e.getChannel()
                        .sendMessage(
                                ":x: You do not have the required permissions to remove this user from the whitelist.")
                        .queue();
                return;
            }
            target = member;
        }

        if (target == null) {
            e.getChannel().sendMessage(":x: Unable to find user.").queue();
            return;
        }

        if (SQL.removeEntry(target.getId())) {
            e.getChannel().sendMessage(
                    ":white_check_mark: User `" + target.getUser().getAsTag() + "` was removed from the whitelist.")
                    .queue(message -> RoleUtil.removeGrantedRole(e));
        } else {
            e.getChannel().sendMessage(
                    ":x: **Could not remove user from the whitelist!** Perhaps they aren't on it to begin with?")
                    .queue();
        }
    }
}
