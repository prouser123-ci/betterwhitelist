package kokumaji.betterwhitelist.discord;

import kokumaji.betterwhitelist.BetterWhitelist;
import kokumaji.betterwhitelist.listeners.URLRequest;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Date;

public class BotCommandListener extends ListenerAdapter {


    private FileConfiguration plConf = BetterWhitelist.getPlugin().getConfig();
    String filetype = plConf.getString("filetype");

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message msg = e.getMessage();
        String[] msgArr = msg.getContentRaw().split(" ");

        if (e.getAuthor().isBot()) return;

        String prefix = plConf.getString("discord.prefix");
        String commandName = plConf.getString("discord.validationCommandName");

        if (e.isFromType(ChannelType.PRIVATE)) return;

        if (msgArr[0].equalsIgnoreCase(prefix + commandName)) {
            MessageChannel channel = e.getChannel();
            String username = msgArr[1];
            String message = plConf.getString("discord.validationSuccess");
            String errmessage = plConf.getString("discord.validationError");

            if (filetype.toLowerCase().contains("file")) {
                int response = 0;
                try {
                    response = URLRequest.checkUsername(username);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                if (response == 200 && response != 204) {

                    OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(username);

                    p.setWhitelisted(true);

                    channel.sendMessage(message).queue();
                    BetterWhitelist.writeToCSV(p.getUniqueId().toString(), e.getAuthor().getId(), Long.toString(new Date().getTime()));

                } else {
                    channel.sendMessage(errmessage).queue();
                }
            }

        }
    }
}
