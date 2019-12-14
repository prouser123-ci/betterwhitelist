package kokumaji.betterwhitelist.discord;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;

public class BotMain implements Listener {

    private static JDA jda = null;
    public void init(String token) throws LoginException {
        try {
            JDA jda = new JDABuilder(AccountType.BOT).setToken(token).build();
            jda.addEventListener(new BotCommandListener());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static JDA getJDA() {
        return getJDA();
    }

}