package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import net.md_5.bungee.api.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLConnection {

    private static String sqlHost = PluginConfig.getConfig().getString("mysql.host");
    private static String sqlDatabase = PluginConfig.getConfig().getString("mysql.database");
    private static String sqlPort = PluginConfig.getConfig().getString("mysql.port");
    private static String sqlUsername = PluginConfig.getConfig().getString("mysql.username");
    private static String sqlPassword = PluginConfig.getConfig().getString("mysql.password");

    private static Boolean enabled = PluginConfig.getConfig().getBoolean("enableSql");

    private SQLConnection() { }

    public static void checkTable() {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":"+ sqlPort + "/" + sqlDatabase, sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();
            String checkTable = "CREATE TABLE IF NOT EXISTS `minecraft_whitelist` (`discordID` VARCHAR(20),`minecraft_uuid` VARCHAR(36));";
            stmt.executeUpdate(checkTable);

            con.close();
        }

        catch (Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n" +ChatColor.RED + "STACKTRACE:" + e);
        }

    }

    public static String getDiscordIDFromMinecraft(String pUUID) {
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":"+ sqlPort + "/" + sqlDatabase, sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select discordID from minecraft_whitelist where minecraft_uuid = '" + pUUID + "'");
            while(rs.next()) {
                String discordID = rs.getString(1);
                con.close();

                return discordID;
            }
            return null;

        }

        catch(Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n" +ChatColor.RED + "STACKTRACE:" + e);
        }

        return null;

    }

    public static String getMinecraftFromDiscordID(String pDiscorID) {
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":"+ sqlPort + "/" + sqlDatabase, sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select minecraft_uuid from minecraft_whitelist where discordID = " + pDiscorID);
            while(rs.next()) {
                String mcUUID = rs.getString(1);

                con.close();

                return mcUUID;
            }
            return null;

        }

        catch(Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n" +ChatColor.RED + "STACKTRACE:" + e);
        }

        return null;

    }

    public static void addEntry(String pUUID, String pdiscordID) {
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":"+ sqlPort + "/" + sqlDatabase, sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            stmt.executeUpdate("INSERT IGNORE INTO minecraft_whitelist (discordID, minecraft_uuid) VALUES ('" + pdiscordID + "','" + pUUID + "');");

            con.close();

        }

        catch(Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n" +ChatColor.RED + "STACKTRACE:" + e);
        }
    }

    public static void removeEntry(String discordID) {
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":"+ sqlPort + "/" + sqlDatabase, sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            stmt.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `discordID` = '" + discordID + "'");

            con.close();

        }

        catch(Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n" +ChatColor.RED + "STACKTRACE:" + e);
        }
    }

}
