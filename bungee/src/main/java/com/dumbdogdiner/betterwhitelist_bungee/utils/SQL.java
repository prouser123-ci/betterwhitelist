package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

/**
 * SQL wrapper for fetching/storing user whitelist data.
 */
public class SQL {

    private static String databaseUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true",
            PluginConfig.getConfig().getString("mysql.host"), PluginConfig.getConfig().getString("mysql.port"),
            PluginConfig.getConfig().getString("mysql.database"));

    private static Boolean enabled = PluginConfig.getConfig().getBoolean("enableSql");

    private static HikariDataSource ds;

    public static void init() {
        // Create and configure SQL configuration.
        var config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(PluginConfig.getConfig().getString("mysql.username"));
        config.setPassword(PluginConfig.getConfig().getString("mysql.password"));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);

        checkTable();
    }

    /**
     * Fetch a connection from the pool, and use it to create a new SQL statement.
     * 
     * @return
     * @throws Exception
     */
    private static Statement createStatement() throws Exception {
        if (!enabled) {
            throw new Exception("SQL disabled.");
        }

        return ds.getConnection().createStatement();
    }

    /**
     * Handle and print SQL errors to console.
     */
    private static void handleSQLError(Exception e) {
        BetterWhitelistBungee.getInstance().getLogger().severe("Failed to execute SQL statement.");
        e.printStackTrace();

    }

    /**
     * Check that the SQL table storing player UUIDs is valid.
     */
    public static void checkTable() {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .warning("SQL connection has been disabled in 'config.yml'.");
            return;
        }

        BetterWhitelistBungee.getInstance().getLogger().info("[sql] Checking the UUID table is valid...");

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
     * Check, and upgrade the SQL table - for later.
     */
    private static void upgradeTable() {
        if (!checkIfUpgradeable()) {
            return;
        }

        BetterWhitelistBungee.getInstance().getLogger().info("[sql] Upgrading table...");

        try {
            var statement = createStatement();
            var update = "ALTER TABLE `minecraft_whitelist` RENAME COLUMN `discordID` TO `discord_id`";

            statement.executeUpdate(update);

            update = "";

        } catch (Exception e) {
            handleSQLError(e);
        }
    }

    /**
     * Checks if the table can be upgraded.
     */
    private static boolean checkIfUpgradeable() {

        try {
            var result = createStatement().executeQuery(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = `minecraft_whitelist`");

            while (result.next()) {
                if (result.getString(1) == "discord_id") {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Fetch a user's Discord ID from their Minecraft UUID.
     * 
     * @param uuid
     * @return
     */
    public static String getDiscordIDFromMinecraft(String uuid) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .warning("SQL connection has been disabled in 'config.yml'.");
            return null;
        }

        try {
            var statement = createStatement();
            var result = statement.executeQuery(
                    "SELECT `discordID` FROM `minecraft_whitelist` WHERE `minecraft_uuid`='" + uuid + "'");

            // Return the first result.
            while (result.next()) {
                var id = result.getString(1);
                statement.close();
                return id;
            }

            // If not found, return null.
            return null;
        }

        catch (Exception e) {
            handleSQLError(e);
            return null;
        }
    }

    /**
     * Fetch a user's Minecraft UUID from their Discord ID.
     * 
     * @param discordID
     * @return
     */
    public static String getUuidFromDiscordId(String discordID) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .warning("SQL connection has been disabled in 'config.yml'.");
            return null;
        }

        try {
            var statement = createStatement();
            var result = statement.executeQuery(
                    "SELECT `minecraft_uuid` FROM `minecraft_whitelist` WHERE `discordID`='" + discordID + "'");

            if (result.next()) {
                var uuid = result.getString(1);
                statement.close();

                // BetterWhitelistBungee.getInstance().getLogger().info("Got '" + uuid + "' for
                // ID '" + discordID + "'.");
                return uuid;
            }

            return null;
        }

        catch (Exception e) {
            handleSQLError(e);
            return null;
        }
    }

    /**
     * Add a user to the SQL database.
     * 
     * @param discordID
     * @param uuid
     */
    public static boolean addEntry(String discordID, String uuid) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("INSERT IGNORE INTO `minecraft_whitelist` (`discordID`, `minecraft_uuid`) VALUES ('"
                    + discordID + "','" + uuid + "');");
            statement.close();

            BetterWhitelistBungee.getInstance().getLogger()
                    .info("Added whitelist entry: '" + discordID + "' => '" + uuid + "'");

            return true;
        }

        catch (Exception e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Remove a user from the database using their Discord ID.
     * 
     * @param discordID
     */
    public static boolean removeEntry(String discordID) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        // Check to make sure entry exists to be removed.
        if (getUuidFromDiscordId(discordID) == null) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .warning("Request to remove non-existent whitelist entry for ID '" + discordID + "'.");
            return false;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `discordID`='" + discordID + "'");
            statement.close();

            BetterWhitelistBungee.getInstance().getLogger().info("Removed whitelist entry: '" + discordID + "'");

            return true;
        }

        catch (Exception e) {
            handleSQLError(e);
            return false;
        }
    }

    /**
     * Remove a user from the database using their Minecraft UUID.
     * 
     * @param uuid
     */
    public static boolean removeEntryUsingUuid(String uuid) {
        if (!enabled) {
            BetterWhitelistBungee.getInstance().getLogger()
                    .warning("SQL connection has been disabled in 'config.yml'.");
            return false;
        }

        try {
            var statement = createStatement();
            statement.executeUpdate("DELETE FROM `minecraft_whitelist` WHERE `minecraft_uuid`='" + uuid + "'");
            statement.close();
            return true;
        }

        catch (Exception e) {
            handleSQLError(e);
            return false;
        }
    }
}
