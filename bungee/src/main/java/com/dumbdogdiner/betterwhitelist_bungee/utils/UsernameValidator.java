package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.google.gson.Gson;

import javax.ws.rs.client.ClientBuilder;

/**
 * Class for validating Minecraft usernames.
 */
public class UsernameValidator {

    /**
     * Fetch the UUID of a player from their Minecraft username.
     * @param username
     * @return
     *
     * TODO: This throws a whole bunch of warning errors into the console for seemingly no reason - e.g.
     * HK2 service reification failed for [org.glassfish.jersey.message.internal.XmlRootElementJaxbProvider$General] with an exception:
     *
     * MultiException stack 1 of 2
     * java.lang.NoClassDefFoundError: javax/xml/bind/JAXBException
     *
     * MultiException stack 2 of 2
     * java.lang.IllegalArgumentException: Errors were discovered while reifying SystemDescriptor
     *
     * Both exceptions triggered by the get method in line 32.
     * The code, however, works as intended.
     */
    public static MojangUser getUser(String username) {
        // Make request to Mojang and decode JSON body.
        var target = ClientBuilder.newClient().target(formUrl(username));
        var result = new Gson().fromJson(target.request().get(String.class), MojangUser.class);

        BetterWhitelistBungee.getInstance().getLogger().info(String.format("Got UUID '%s' for user '%s'.", result.id, result.name));

        return result;
    }

    /**
     * Forms the base URL for the Mojang API request.
     * @param username
     * @return
     */
    private static String formUrl(String username) {
        String baseUrl = "https://api.mojang.com/users/profiles/minecraft/";
        return String.format("%s%s?at=%s", baseUrl, username, System.currentTimeMillis() / 1000L);
    }
}
