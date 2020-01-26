package com.dumbdogdiner.betterwhitelist.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.dumbdogdiner.betterwhitelist.BetterWhitelist;

import net.md_5.bungee.api.ChatColor;

public class MySQLRequest {

    private static String sqlHost = BetterWhitelist.getPlugin().getConfig().getString("mysql.host");
    private static String sqlDatabase = BetterWhitelist.getPlugin().getConfig().getString("mysql.database");
    private static String sqlPort = BetterWhitelist.getPlugin().getConfig().getString("mysql.port");
    private static String sqlUsername = BetterWhitelist.getPlugin().getConfig().getString("mysql.username");
    private static String sqlPassword = BetterWhitelist.getPlugin().getConfig().getString("mysql.password");

    public static void checkTable() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase,
                    sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();
            String checkTable = "CREATE TABLE IF NOT EXISTS `minecraft_whitelist` (`discordID` VARCHAR(20),`minecraft_uuid` VARCHAR(36));";
            stmt.executeUpdate(checkTable);

            con.close();

        }

        catch (Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n"
                    + ChatColor.RED + "STACKTRACE:" + e);
        }

    }

    public static String getDiscordIDFromMinecraft(String pUUID) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase,
                    sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            ResultSet rs = stmt
                    .executeQuery("select discordID from minecraft_whitelist where minecraft_uuid = '" + pUUID + "'");
            while (rs.next()) {
                String discordID = rs.getString(1);
                con.close();

                return discordID;
            }
            return null;
        }

        catch (Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n"
                    + ChatColor.RED + "STACKTRACE:" + e);
        }

        return null;
    }

    public static String getMinecraftFromDiscordID(String pDiscorID) {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase,
                    sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            ResultSet rs = stmt
                    .executeQuery("select minecraft_uuid from minecraft_whitelist where discordID = " + pDiscorID);
            while (rs.next()) {
                String mcUUID = rs.getString(1);

                con.close();

                return mcUUID;
            }
            return null;

        }

        catch (Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n"
                    + ChatColor.RED + "STACKTRACE:" + e);
        }

        return null;

    }

    public static void addEntry(String pUUID, String pdiscordID) {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase,
                    sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            stmt.executeUpdate("INSERT IGNORE INTO minecraft_whitelist (discordID, minecraft_uuid) VALUES ('"
                    + pdiscordID + "','" + pUUID + "');");

            con.close();

        }

        catch (Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n"
                    + ChatColor.RED + "STACKTRACE:" + e);
        }
    }

    public static void removeEntry(String discordID) {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase,
                    sqlUsername, sqlPassword);

            Statement stmt = con.createStatement();

            stmt.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `discordID` = '" + discordID + "'");

            con.close();

        }

        catch (Exception e) {
            System.out.println(ChatColor.RED + "[DiscordWhitelister] Error while trying to execute MySQL query\n \n"
                    + ChatColor.RED + "STACKTRACE:" + e);
        }
    }

}
