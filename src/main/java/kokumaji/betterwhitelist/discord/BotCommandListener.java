package kokumaji.betterwhitelist.discord;

import kokumaji.betterwhitelist.BetterWhitelist;
import kokumaji.betterwhitelist.listeners.MySQLRequest;
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
        if (e.getAuthor().isBot()) return;
        Message msg = e.getMessage();
        String[] msgArr = msg.getContentRaw().split(" ");

        if(!BetterWhitelist.getPlugin().getConfig().getBoolean("discord.enableAutoWhitelisting")) {
            e.getChannel().sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.whitelistingDisabled")).queue();
            return;
        }

        String prefix = BetterWhitelist.getPlugin().getConfig().getString("discord.prefix");
        String commandName = BetterWhitelist.getPlugin().getConfig().getString("discord.validationCommandName");

        if (e.isFromType(ChannelType.PRIVATE)) return;

        if (msgArr[0].equalsIgnoreCase(prefix + commandName)) {
            MessageChannel channel = e.getChannel();
            String username = msgArr[1];

            if(BetterWhitelist.getPlugin().getConfig().getBoolean("oneAccountPerUser")) {

                if(BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("file")) {
                    Reader reader = Files.newBufferedReader(Paths.get(BetterWhitelist.getPlugin().getDataFolder() + "/userdata.csv"));
                    List<String[]> userData = BetterWhitelist.getUserData(reader);
                    boolean userFound = false;

                    for (String[] current : userData) {
                        if (current[1].equals(e.getAuthor().getId())) {
                            userFound = true;
                            break;
                        }
                    }

                    if(!userFound) {
                        if(BetterWhitelist.getPlugin().getConfig().getBoolean("discord.reqRole.enabled")) {
                            if(!(e.getMember().getRoles().contains(e.getGuild().getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.roleid"))))) {
                                channel.sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.missingRoleError")).queue();
                            } else {
                                addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                            }
                        } else {
                            addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                        }
                    } else {
                        e.getChannel().sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.userAlreadyWhitelisted")).queue();
                    }
                }
                else if(BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("sql")) {
                    String pUUID = MySQLRequest.getMinecraftFromDiscordID(e.getAuthor().getId());
                    if(pUUID == null) {
                        if(BetterWhitelist.getPlugin().getConfig().getBoolean("discord.reqRole.enabled")) {
                            if(!(e.getMember().getRoles().contains(e.getGuild().getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.roleid"))))) {
                                channel.sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.missingRoleError")).queue();
                            } else {
                                addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                            }
                        } else {
                            addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                        }
                    } else {
                        e.getChannel().sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.userAlreadyWhitelisted")).queue();
                    }

                }

            } else {
                if(BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("file")) {
                    if(BetterWhitelist.getPlugin().getConfig().getBoolean("discord.reqRole.enabled")) {
                        if(!(e.getMember().getRoles().contains(e.getGuild().getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.roleid"))))) {
                            channel.sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.missingRoleError")).queue();
                        } else {
                            addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                        }
                    } else {
                        addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                    }
                } else if(BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("sql")) {
                    if(BetterWhitelist.getPlugin().getConfig().getBoolean("discord.reqRole.enabled")) {
                        if(!(e.getMember().getRoles().contains(e.getGuild().getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.reqRole.roleid"))))) {
                            channel.sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.missingRoleError")).queue();
                        } else {
                            addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                        }
                    } else {
                        addUserToWhitelist(username, channel, e.getGuild().getMember(e.getAuthor()), e.getGuild());
                    }
                }

            }



        }
    }

    private void addUserToWhitelist(String pUsername, MessageChannel pChannel, Member pMember, Guild pGuild) {
        String message = BetterWhitelist.getPlugin().getConfig().getString("lang.validationSuccess");
        String errmessage = BetterWhitelist.getPlugin().getConfig().getString("lang.validationError");

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
            if(BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("file")) {
                BetterWhitelist.writeToCSV(p.getUniqueId().toString(), pMember.getId(), Long.toString(new Date().getTime()));
            } else if(BetterWhitelist.getPlugin().getConfig().getString("filetype").contains("sql")) {
                MySQLRequest.addEntry(p.getUniqueId().toString(), pMember.getId());
            }


            if(BetterWhitelist.getPlugin().getConfig().getBoolean("discord.giveRole.enabled")) {
                if(!pMember.getRoles().contains(pGuild.getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.giveRole.roleid")))) {
                    pGuild.addRoleToMember(pMember, pGuild.getRoleById(BetterWhitelist.getPlugin().getConfig().getString("discord.giveRole.roleid"))).queue();
                    if(!BetterWhitelist.getPlugin().getConfig().getBoolean("discord.giveRole.silent")) {
                        pChannel.sendMessage(BetterWhitelist.getPlugin().getConfig().getString("lang.giveRoleMessage")).queue();
                    }

                }
            }

        } else {
            pChannel.sendMessage(errmessage).queue();
        }
    }
}
