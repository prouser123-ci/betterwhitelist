package com.dumbdogdiner.betterwhitelist_bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * A class with methods for communicating between server instances.
 */
public class InstanceMessenger implements Listener {
    public BetterWhitelistBungeePlugin plugin;

    public InstanceMessenger(BetterWhitelistBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

        String subChannel = in.readUTF();
        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

        String somedata = "";
        try {
            somedata = msgin.readUTF();
        } catch (IOException err) {
            plugin.getLogger().warning("Failed to decode received PluginMessageEvent.");
            err.printStackTrace();
        }


        plugin.getLogger().info("msg => " + subChannel + " " + somedata);

        if (!e.getTag().equalsIgnoreCase("BungeeCord")) {
            return;
        }
    }
}
