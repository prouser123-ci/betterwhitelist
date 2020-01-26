package com.dumbdogdiner.betterwhitelist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.dumbdogdiner.betterwhitelist.BetterWhitelist;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.BanList.Type;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeMessenger implements PluginMessageListener {
    public BetterWhitelist plugin;

    private boolean banSyncEnabled = BetterWhitelist.getPlugin().getConfig().getBoolean("enableBanSync");

    public BungeeMessenger(BetterWhitelist plugin) {
        this.plugin = plugin;
    }

    public void banPlayerGlobal(Player player, UUID target) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        Bukkit.getLogger().info("Requesting player UUID '" + target.toString() + "' be banned globally...");

        out.writeUTF("Forward");
        out.writeUTF("ONLINE");
        out.writeUTF("Ban");
        out.writeUTF(target.toString());

        player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player receiver, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (!subchannel.equals("Forward")) {
            return;
        }

        // Read forwarded command - for some reason it's done like this.
        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);
        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

        String command;

        // This shouldn't be necessary but VS Code threw a fit at me.
        try {
            command = msgin.readUTF();
        } catch (IOException err) {
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
            return;
        }
    }

    /**
     * Handle a request to ban a player from the server.
     * 
     * @param uuidToBan
     */
    public void handleBanRequest(String uuidToBan) {
        if (banSyncEnabled == false) {
            return;
        }

        Bukkit.getLogger().info("Request to ban user '" + uuidToBan + "' from Bungee.");

        Player onlinePlayer = Bukkit.getPlayer(UUID.fromString(uuidToBan));

        // If player was found
        if (!onlinePlayer.equals(null) && !onlinePlayer.isBanned()) {
            onlinePlayer.kickPlayer("Banned from server.");
            Bukkit.getBanList(Type.NAME).addBan(onlinePlayer.getName(), "Banned from server.", null, null);
        }
    }

    /**
     * Handle a request from another server to check if a player is banned.
     * 
     * @param receiver
     * @param uuidToCheck
     */
    public void handleCheckBanRequest(Player receiver, String uuidToCheck) {
        Bukkit.getLogger().info("Checking if UUID '" + uuidToCheck + "' should be banned globally...");

        Player player = Bukkit.getPlayer(UUID.fromString(uuidToCheck));

        if (player.isBanned()) {
            sendEvent(receiver, "Ban", uuidToCheck);
        }
    }

    /**
     * Send an event to other plugins connected to Bungeecord.
     * 
     * Defaults to messaging all ONLINE servers.
     */
    void sendEvent(Player receiver, String subChannel, String... args) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF("ONLINE");
        out.writeUTF(subChannel);

        for (String arg : args) {
            out.writeUTF(arg);
        }

        receiver.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}