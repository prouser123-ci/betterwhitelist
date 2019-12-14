package kokumaji.betterwhitelist.discord;

import kokumaji.betterwhitelist.BetterWhitelist;
import net.dv8tion.jda.api.JDA;

public class DiscordBot {

    public static JDA getJda() {
        return BetterWhitelist.getJda();
    }
}
