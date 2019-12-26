package kokumaji.betterwhitelist;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import kokumaji.betterwhitelist.commands.BetterWhitelistCommand;
import kokumaji.betterwhitelist.discord.BotCommandListener;
import kokumaji.betterwhitelist.discord.GuildBanListener;
import kokumaji.betterwhitelist.listeners.MinecraftBanListener;
import kokumaji.betterwhitelist.listeners.MySQLRequest;
import lombok.Getter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class BetterWhitelist extends JavaPlugin {

    @Getter
    private static JDA Jda = null;

    private FileConfiguration config = this.getConfig();

    public static Plugin getPlugin() {
        return getPlugin(BetterWhitelist.class);
    }

    public static void initBot() {
        String discordToken = getPlugin().getConfig().getString("discord.token");
        try {
            Jda = new JDABuilder(AccountType.BOT).setToken(discordToken).build().awaitReady();
            Jda.addEventListener(new BotCommandListener());
            Jda.addEventListener(new GuildBanListener());
        } catch (LoginException | InterruptedException ignored) {
            System.out.println("[BetterWhitelist] Could not initialize bot. Please change the discord.token entry in the config file.");
        }
    }

    public static void writeToCSV(String pUUID, String pDiscordID, String pJoinedAt) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(getPlugin().getDataFolder() + "/userdata.csv", true));

            String[] userDataLine = {pUUID, pDiscordID, pJoinedAt};
            writer.writeNext(userDataLine);

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getUserData(Reader reader) throws IOException {
        List<String[]> list;
        CSVReader csvReader = new CSVReader(reader);

        list = csvReader.readAll();

        reader.close();
        csvReader.close();
        return list;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        initBot();
        this.getCommand("betterwhitelist").setExecutor(new BetterWhitelistCommand());
        Bukkit.getPluginManager().registerEvents(new MinecraftBanListener(), this);
        if(getConfig().getString("filetype").contains("sql")) {
            MySQLRequest.checkTable();
        } else if(getConfig().getString("filetype").contains("file") || getConfig().getString("filetype").contains("csv")) {
            File userDataFile = new File(getPlugin().getDataFolder() + "/userdata.csv");
            if (!userDataFile.exists()) {
                try {
                    userDataFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        BetterWhitelist.getJda().shutdown();
    }

}
