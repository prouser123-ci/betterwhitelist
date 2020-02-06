package com.dumbdogdiner.betterwhitelist.utils;

import com.dumbdogdiner.betterwhitelist.BetterWhitelist;

import java.sql.DriverManager;
import java.sql.Statement;

/**
 * SQL wrapper for fetching/storing user whitelist data.
 */
public class SQLConnection {

    // TODO: Use a pooled connection.

    private static String databaseUrl = String.format(
            "jdbc:mysql://%s:%s/%s",
            BetterWhitelist.getInstance().getConfig().getString("mysql.host"),
            BetterWhitelist.getInstance().getConfig().getString("mysql.port"),
            BetterWhitelist.getInstance().getConfig().getString("mysql.database")

    );

    private static String sqlUsername = BetterWhitelist.getInstance().getConfig().getString("mysql.username");
    private static String sqlPassword = BetterWhitelist.getInstance().getConfig().getString("mysql.password");

    private static Boolean enabled = BetterWhitelist.getInstance().getConfig().getBoolean("enableSql");

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
        BetterWhitelist.getInstance().getInstance().getLogger().severe("Failed to execute SQL statement.");
        e.printStackTrace();
        return;
    }

    /**
     * Check that the SQL table storing player UUIDs is valid.
     */
    public static void checkTable() {
        if (!enabled) {
            BetterWhitelist.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        BetterWhitelist.getInstance().getLogger().info("[sql] Checking the UUID table is valid...");

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
            BetterWhitelist.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
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
            BetterWhitelist.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
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
    public static boolean addEntry(String discordID, String uuid) {
        if (!enabled) {
            BetterWhitelist.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("INSERT IGNORE INTO minecraft_whitelist (discordID, minecraft_uuid) VALUES ('" + discordID + "','" + uuid + "');");
            statement.getConnection().close();
            return true;
        }

        catch(Exception e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Remove a user from the database using their Discord ID.
     * @param discordID
     */
    public static boolean removeEntry(String discordID) {
        if (!enabled) {
            BetterWhitelist.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `discordID` = '" + discordID + "'");
            statement.getConnection().close();
            return true;
        }

        catch(Exception e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Remove a user from the database using their Minecraft UUID.
     * @param uuid
     */
    public static boolean removeEntryUsingUuid(String uuid) {
        if (!enabled) {
            BetterWhitelist.getInstance().getLogger().warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `minecraft_uuid` = '" + uuid + "'");
            statement.getConnection().close();
            return true;
        }

        catch(Exception e) {
            handleSQLError(e);
            return false;
        }
    }
}
