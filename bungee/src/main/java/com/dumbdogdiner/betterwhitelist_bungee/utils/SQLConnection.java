package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;

import java.sql.*;

public class SQLConnection {

    private static String databaseUrl = String.format(
            "jdbc:mysql://%s:%s/%s",
            PluginConfig.getConfig().getString("mysql.host"),
            PluginConfig.getConfig().getString("mysql.database"),
            PluginConfig.getConfig().getString("mysql.port")
    );

    private static String sqlUsername = PluginConfig.getConfig().getString("mysql.username");
    private static String sqlPassword = PluginConfig.getConfig().getString("mysql.password");

    private static Boolean enabled = PluginConfig.getConfig().getBoolean("enableSql");

    private SQLConnection() { }

    /**
     * Create a connection to MySQL.
     * @return
     * @throws Exception
     */
    private static Statement createStatement() throws Exception {
        if (!enabled) {
            throw new Exception("SQL disabled.");
        }

        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(databaseUrl, sqlUsername, sqlPassword).createStatement();
    }

    private static void handleSQLError(Exception e) {
        BetterWhitelistBungee.getInstance().getLogger().severe("Failed to execute SQL statement.");
        e.printStackTrace();
        return;
    }

    public static void checkTable() {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        try {
            var statement = createStatement();
            String checkTable = "CREATE TABLE IF NOT EXISTS `minecraft_whitelist` (`discordID` VARCHAR(20),`minecraft_uuid` VARCHAR(36));";
            statement.executeUpdate(checkTable);
            statement.getConnection().close();
        }

        catch (Exception e) {
            handleSQLError(e);
        }
    }

    /**
     * Fetch a user's Discord ID from their Minecraft UUID.
     * @param uuid
     * @return
     */
    public static String getDiscordIDFromMinecraft(String uuid) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return null;
        }

        try {
            var statement = createStatement();
            var result = statement.executeQuery("select discordID from minecraft_whitelist where minecraft_uuid = '" + uuid + "'");

            // Return the first result.
            while(result.next()) {
                String discordID = result.getString(1);
                statement.getConnection().close();
                return discordID;
            }
            return null;
        }

        catch(Exception e) {
           handleSQLError(e);
           return null;
        }
    }

    /**
     * Fetch a user's Minecraft UUID from their Discord ID.
     * @param discordID
     * @return
     */
    public static String getUuidFromDiscordId(String discordID) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return null;
        }

        try {
            var statement = createStatement();
            var result = statement.executeQuery("select minecraft_uuid from minecraft_whitelist where discordID = " + discordID);

            while(result.next()) {
                statement.getConnection().close();
                return result.getString(1);
            }
            return null;
        }

        catch(Exception e) {
            handleSQLError(e);
            return null;
        }
    }

    /**
     * Add a user to the SQL database.
     * @param discordID
     * @param uuid
     */
    public static void addEntry(String discordID, String uuid) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("INSERT IGNORE INTO minecraft_whitelist (discordID, minecraft_uuid) VALUES ('" + discordID + "','" + uuid + "');");
            statement.getConnection().close();
        }

        catch(Exception e) {
            handleSQLError(e);
        }
    }

    /**
     * Remove a user from the database using their Discord ID.
     * @param discordID
     */
    public static void removeEntry(String discordID) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `discordID` = '" + discordID + "'");
            statement.getConnection().close();
        }

        catch(Exception e) {
            handleSQLError(e);
        }
    }

    /**
     * Remove a user from the database using their Minecraft UUID.
     * @param uuid
     */
    public static void removeEntryUsingUuid(String uuid) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `minecraft_uuid` = '" + uuid + "'");
            statement.getConnection().close();
        }

        catch(Exception e) {
            handleSQLError(e);
        }
    }
}
