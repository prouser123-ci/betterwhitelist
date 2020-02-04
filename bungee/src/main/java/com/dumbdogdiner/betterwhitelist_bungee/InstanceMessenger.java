package com.dumbdogdiner.betterwhitelist_bungee;

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

    private static String channel = "btw:bungee";
    public static String getChannel() {
        return channel;
    }

    private static Boolean didFail = false;

    private static InstanceMessenger instance;
    public static InstanceMessenger getInstance() {
        if (instance != null) {
            return instance;
        }

        return instance = new InstanceMessenger();
    }

    private InstanceMessenger() { }

    @EventHandler
    public static void on(PluginMessageEvent e) {
        var plugin = BetterWhitelistBungee.getInstance();

        plugin.getLogger().info(String.format("Received %d bytes on channel '%s'.", e.getData().length, e.getTag()));

        if (didFail) {
            plugin.getLogger().warning(
                    "Skipping handling event from '" + e.getReceiver().getAddress() + "' - handling has failed previously"
            );
            return;
        }

        // Prevent handling if channel does not match + debug logging
        if (!e.getTag().equalsIgnoreCase(channel)) {
            plugin.getLogger().info("Ignoring message on channel '" + e.getTag() + "'.");
            return;
        } else {
            plugin.getLogger().info("Got message on '" + channel + "' - decoding...");
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));

        String subChannel;
        DataInputStream msgin;
        String somedata = "";

        try {
            subChannel = in.readUTF();
            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            somedata = msgin.readUTF();

        } catch (IOException err) {
            plugin.getLogger().severe("Failed to decode received PluginMessageEvent.");
            err.printStackTrace();
            plugin.getLogger().warning("Future messages will not be handled.");
            didFail = true;
            return;
        }



        plugin.getLogger().info("msg => " + subChannel + " " + somedata);

        if (!e.getTag().equalsIgnoreCase(channel)) {
            return;
        }
    }
}
