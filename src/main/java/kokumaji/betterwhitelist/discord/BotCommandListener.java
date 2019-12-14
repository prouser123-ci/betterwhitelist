package kokumaji.betterwhitelist.discord;

import kokumaji.betterwhitelist.BetterWhitelist;
import kokumaji.betterwhitelist.listeners.URLRequest;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class BotCommandListener extends ListenerAdapter {


	private FileConfiguration plConf = BetterWhitelist.getPlugin().getConfig();
    String filetype = plConf.getString("filetype");

    @Override
    public void onMessageReceived(MessageReceivedEvent e)
    {
        Message msg = e.getMessage();
        String[] msgArr = msg.getContentRaw().split(" ");
        
        if(e.getAuthor().isBot()) return;

    	String prefix = plConf.getString("discord.prefix");
    	String commandName = plConf.getString("discord.validationCommandName");
        
        if(e.isFromType(ChannelType.PRIVATE)) return;
        
        if (msgArr[0].equalsIgnoreCase(prefix + commandName))
        {
            MessageChannel channel = e.getChannel();
            String username = msgArr[1];
            String message = plConf.getString("discord.validationSuccess");
            String errmessage = plConf.getString("discord.validationError");

            if(filetype.toLowerCase().contains("file")) {
                int response = 0;
                try {
                    response = URLRequest.checkUsername(username);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                if(response == 200 && response != 204) {

                    OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(username);

                    p.setWhitelisted(true);

                    channel.sendMessage(message).queue();
                    File playerYML = new File(BetterWhitelist.getPlugin().getDataFolder() + "/players/" + username + ".yml");
                    if(!playerYML.exists()) {
                        try {
                            File playerDIR = new File(BetterWhitelist.getPlugin().getDataFolder() + "/players/");
                            if(!playerDIR.exists()) {
                                playerDIR.mkdir();
                            }
                            playerYML.createNewFile();
                            FileConfiguration playerConf = YamlConfiguration.loadConfiguration(playerYML);
                            playerConf.set("uuid", p.getUniqueId().toString());
                            playerConf.set("discordid", e.getAuthor().getId());
                            playerConf.set("whitelistedOn", new Date().getTime());

                            playerConf.save(playerYML);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                } else {
                    channel.sendMessage(errmessage).queue();
                }
            }

        }
    }
}
