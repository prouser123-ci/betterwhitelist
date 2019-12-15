package kokumaji.betterwhitelist.discord;

import kokumaji.betterwhitelist.BetterWhitelist;
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

public class GuildBanListener extends ListenerAdapter {

    private FileConfiguration plConf = BetterWhitelist.getPlugin().getConfig();
    private boolean banSyncEnabled = plConf.getBoolean("enableBanSync");

    @SneakyThrows
    @Override
    public void onGuildBan(GuildBanEvent e) {

        if (banSyncEnabled) {
            User user = e.getUser();
            Reader reader = null;
            List<String[]> userData = null;

            reader = Files.newBufferedReader(Paths.get(BetterWhitelist.getPlugin().getDataFolder() + "/userdata.csv"));
            userData = BetterWhitelist.getUserData(reader);

            for (int i = 0; i < userData.size(); i++) {
                String[] current = userData.get(i);

                if (current[1].equals(user.getId())) {

                    OfflinePlayer player = Bukkit.getOfflinePlayer(current[0]);
                    Bukkit.getPlayer(player.getUniqueId()).kickPlayer("You've been banned on Discord.");
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "You've been banned from Discord", null, "console");
                    break;
                }

            }
        }

    }
}
