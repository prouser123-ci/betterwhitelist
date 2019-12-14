package kokumaji.betterwhitelist;

import kokumaji.betterwhitelist.commands.BetterWhitelistCommand;
import kokumaji.betterwhitelist.discord.BotMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public final class BetterWhitelist extends JavaPlugin {

    private FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getCommand("betterwhitelist").setExecutor(new BetterWhitelistCommand());
        String discordToken = config.getString("discord.token");
        if(discordToken.contains("TOKEN")) {
            System.out.println("[DiscordWhitelister] Token not defined in config file. Skipping bot initialization.");
        }
        else {
            BotMain bot = new BotMain();
            try {
                bot.init(discordToken);
            } catch (LoginException e) {

            }
        }

    }

    @Override
    public void onDisable() {
        this.saveConfig();
    }

    public static Plugin getPlugin() {

        return getPlugin(BetterWhitelist.class);
    }
}
