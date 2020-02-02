package com.dumbdogdiner.betterwhitelist_client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

// I know * imports are bad practice, but intellij is being insistent.
import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

public class BungeeMessenger implements PluginMessageListener {

    private String channel = "btw:bungee";

    private boolean banSyncEnabled;
    private UUID latestBan;

    BungeeMessenger() {
        banSyncEnabled = BetterWhitelist.getInstance().getConfig().getBoolean("enableBanSync");
    }

    public String getChannel() {
        return channel;
    }

    /**
     * Request for a player to be banned globally.
     * @param receiver player to ban
     * @param target UUID of player to ban
     */
    public void addGlobalBan(Player receiver, UUID target) {
        Bukkit.getLogger().info("Requesting player UUID '" + target.toString() + "' be banned globally...");
        sendEvent(receiver, "Ban", target.toString());
    }

    /**
     * Requests the Bungee server to remove a global ban.
     * @param receiver player we want to unban
     * @param target the UUID of the target player to unban
     */
    public void removeGlobalBan(Player receiver, UUID target) {
        Bukkit.getLogger().info("Requesting player UUID '" + target.toString() + "' be pardoned globally...");
        sendEvent(receiver, "Pardon", target.toString());
    }

    /**
     * Asks Bungee whether or not the target UUID has been banned.
     * @param receiver player we're checking to see if they're banned
     * @param target the UUID of the player we're checking to ban.
     */
    public void checkGlobalBan(Player receiver, UUID target) {
        BetterWhitelist.getInstance().getLogger().info("Bungee => Is '" + target.toString() + "' banned?");
        sendEvent(receiver, "IsBanned", target.toString());
    }



    @Override
    public void onPluginMessageReceived(String channel, Player receiver, byte[] message) {
        BetterWhitelist.getInstance().getLogger().info(String.format("Received %d bytes on channel '%s'.", message.length, channel));

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (!subchannel.equals("Forward")) {
            return;
        }

        // Read forwarded command - for some reason it's done like this.
        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);
        DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgbytes));

        String command;

        // This shouldn't be necessary but VS Code threw a fit at me.
        try {
            command = msgIn.readUTF();
        } catch (IOException err) {
            BetterWhitelist.getInstance().getLogger().log(Level.WARNING, "Failed to send message to BungeeCord.");
            err.printStackTrace();
            return;
        }

        if (command.equals("Ban")) {
            String uuidToBan = in.readUTF();
            handleBanRequest(uuidToBan);
            return;
        }

        if (command.equals("IsBanned")) {
            String uuidToCheck = in.readUTF();
            handleCheckBanRequest(receiver, uuidToCheck);
        }
    }

    /**
     * Handle a request to ban a player from the server.
     * 
     * @param uuidToBan uuid of player to ban
     */
    private void handleBanRequest(String uuidToBan) {
        if (!banSyncEnabled) {
            return;
        }

        BetterWhitelist.getInstance().getLogger().info("Request to ban user '" + uuidToBan + "' from Bungee.");

        Player onlinePlayer = Bukkit.getPlayer(UUID.fromString(uuidToBan));

        // If player was found
            if (onlinePlayer != null && onlinePlayer.isOnline() && !onlinePlayer.isBanned()) {
                onlinePlayer.kickPlayer("Banned from server.");
                Bukkit.getBanList(Type.NAME).addBan(onlinePlayer.getName(), "Banned from server.", null, null);
                latestBan = UUID.fromString(uuidToBan);
                BetterWhitelist.getInstance().getLogger().log(Level.INFO,
                        "Player " + onlinePlayer.getName() + " With UUID " + uuidToBan + " banned successfully.");
            } else {
            BetterWhitelist.getInstance().getLogger().log(Level.WARNING, "No player found with UUID " + uuidToBan + ".");
        }
    }


    private void handleCheckBanRequest(Player receiver, String uuidToCheck) {
        BetterWhitelist.getInstance().getLogger().info("Checking if UUID '" + uuidToCheck + "' should be banned globally...");

        Player player = Bukkit.getPlayer(UUID.fromString(uuidToCheck));

        if (player != null && player.isBanned()) {
            sendEvent(receiver, "Ban", uuidToCheck);
        }
    }

    /**
     * Send an event to other plugins connected to BungeeCord.
     * 
     * Defaults to messaging all ONLINE servers.
     */
    private void sendEvent(Player sender, String subChannel, String... args) {
        if (!checkIfBungee()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF(subChannel);

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        for (String arg : args) {
            try {
                msgout.writeUTF(arg);
            } catch (IOException err) {
                BetterWhitelist.getInstance().getLogger().warning("Failed to encode message to BungeeCord.");
                err.printStackTrace();
                return;
            }
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        sender.sendPluginMessage(BetterWhitelist.getInstance(), channel, out.toByteArray());
        BetterWhitelist.getInstance().getLogger().info(String.format("[%s][outgoing] %s - args='%s'", channel, subChannel, String.join("', '",  args)));
    }

    /**
     * Checks if the server is running BungeeCord.
     */
    private Boolean checkIfBungee()
    {
        var logger = BetterWhitelist.getInstance().getLogger();
        var server = BetterWhitelist.getInstance().getServer();

        // we check if the server is Spigot/Paper (because of the spigot.yml file)
        if ( !server.getVersion().contains("Spigot") && !server.getVersion().contains("Paper"))  {
            logger.severe( "BTW isn't running on a Bungee-supporting server! Please swap your JAR out for a Spigot or Paper one and try again.");
            server.getPluginManager().disablePlugin(BetterWhitelist.getInstance());
            return false;
        }
        if (server.spigot().getConfig().getConfigurationSection("settings").getBoolean( "settings.bungeecord")) {
            logger.severe("This server isn't running in Bungee-enabled mode - enable it in 'spigot.yml' by changing the 'bungeecord' path to 'true'.");
            server.getPluginManager().disablePlugin(BetterWhitelist.getInstance());
            return false;
        }
        return true;
    }
}