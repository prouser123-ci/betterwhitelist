package kokumaji.betterwhitelist.discord;

import kokumaji.betterwhitelist.BetterWhitelist;
import kokumaji.betterwhitelist.listeners.URLRequest;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class BotCommandListener extends ListenerAdapter {

    @SneakyThrows
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message msg = e.getMessage();
        String[] msgArr = msg.getContentRaw().split(" ");

        if (e.getAuthor().isBot()) return;

        String prefix = BetterWhitelist.getPlugin().getConfig().getString("discord.prefix");
        String commandName = BetterWhitelist.getPlugin().getConfig().getString("discord.validationCommandName");

        if (e.isFromType(ChannelType.PRIVATE)) return;

        if (msgArr[0].equalsIgnoreCase(prefix + commandName)) {
            MessageChannel channel = e.getChannel();
            String username = msgArr[1];

            if(BetterWhitelist.getPlugin().getConfig().getBoolean("oneAccountPerUser")) {
                Reader reader = Files.newBufferedReader(Paths.get(BetterWhitelist.getPlugin().getDataFolder() + "/userdata.csv"));
                List<String[]> userData = BetterWhitelist.getUserData(reader);
                boolean userFound = false;

                for (int i = 0; i < userData.size(); i++) {
                    String[] current = userData.get(i);

                    if (current[1].equals(e.getAuthor().getId())) {
                        userFound = true;
                        break;
                    }
                }

                if(!userFound) {
                    if(BetterWhitelist.getPlugin().getConfig().getBoolean("discord.reqRole.enabled")) {
                        if(!(e.getMember().getRoles().contains(e.getGuild().getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.roleid"))))) {
                            channel.sendMessage(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.validFailedMsg")).queue();
                            return;
                        } else {
                            addUserToWhitelist(username, channel, e.getAuthor().getId());
                        }
                    } else {
                        addUserToWhitelist(username, channel, e.getAuthor().getId());
                    }
                } else {
                    e.getChannel().sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.userAlreadyWhitelisted")).queue();
                }
            } else {
                if(BetterWhitelist.getPlugin().getConfig().getBoolean("discord.reqRole.enabled")) {
                    if(!(e.getMember().getRoles().contains(e.getGuild().getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.roleid"))))) {
                        channel.sendMessage(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.validFailedMsg")).queue();
                        return;
                    } else {
                        addUserToWhitelist(username, channel, e.getAuthor().getId());
                    }
                } else {
                    addUserToWhitelist(username, channel, e.getAuthor().getId());
                }
            }



        }
    }

    private void addUserToWhitelist(String pUsername, MessageChannel pChannel, String pAuthorID) {
        String message = BetterWhitelist.getPlugin().getConfig().getString("discord.validationSuccess");
        String errmessage = BetterWhitelist.getPlugin().getConfig().getString("discord.validationError");

        int response = 0;
        try {
            response = URLRequest.checkUsername(pUsername);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (response == 200 && response != 204) {

            OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(pUsername);

            p.setWhitelisted(true);

            pChannel.sendMessage(message).queue();
            BetterWhitelist.writeToCSV(p.getUniqueId().toString(), pAuthorID, Long.toString(new Date().getTime()));

        } else {
            pChannel.sendMessage(errmessage).queue();
        }
    }
}
