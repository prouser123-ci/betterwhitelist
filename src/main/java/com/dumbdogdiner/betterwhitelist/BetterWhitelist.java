package com.dumbdogdiner.betterwhitelist;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.dumbdogdiner.betterwhitelist.commands.BetterWhitelistCommand;
import com.dumbdogdiner.betterwhitelist.discord.BotCommandListener;
import com.dumbdogdiner.betterwhitelist.discord.GuildBanListener;
import com.dumbdogdiner.betterwhitelist.discord.GuildLeaveListener;
import com.dumbdogdiner.betterwhitelist.listeners.MinecraftBanListener;
import com.dumbdogdiner.betterwhitelist.listeners.MySQLRequest;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class BetterWhitelist extends JavaPlugin {

    private static JDA Jda = null;

    public static JDA getJda() {
        return Jda;
    }

    private BungeeMessenger bungeeMessenger = new BungeeMessenger(this);

    public static Plugin getPlugin() {
        return getPlugin(BetterWhitelist.class);
    }

    public void initBot() {
        String discordToken = getPlugin().getConfig().getString("discord.token");

        try {
            Jda = new JDABuilder(AccountType.BOT).setToken(discordToken).build().awaitReady();
            Jda.addEventListener(new BotCommandListener());
            Jda.addEventListener(new GuildBanListener());
            Jda.addEventListener(new GuildLeaveListener());
        } catch (LoginException | InterruptedException ignored) {
            System.out.println(
                    "[BetterWhitelist] Could not initialize bot. Please change the discord.token entry in the config file.");
        }
    }

    public static void writeToCSV(String playerUUID, String playerDiscordID, String joinedAt) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(getPlugin().getDataFolder() + "/userdata.csv", true));

            String[] userDataLine = { playerUUID, playerDiscordID, joinedAt };
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

        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeeMessenger);

        if (getConfig().getString("filetype").contains("sql")) {
            MySQLRequest.checkTable();
            return;
        }

        else if (getConfig().getString("filetype").contains("file")
                || getConfig().getString("filetype").contains("csv")) {
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
        if (getJda() != null) {
            BetterWhitelist.getJda().shutdown();
        }
    }

}
