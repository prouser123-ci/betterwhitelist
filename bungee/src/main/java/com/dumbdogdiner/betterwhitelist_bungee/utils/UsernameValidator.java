package com.dumbdogdiner.betterwhitelist_bungee.utils;

import com.google.gson.Gson;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Class for validating Minecraft usernames.
 */
public class UsernameValidator {

    /**
     * Fetch the UUID of a player from their Minecraft username.
     * 
     * @param username The name of the user to fetch from the API.
     * @return Mojang API user object
     */
    public static MojangUser getUser(String username) {
        // Make request to Mojang and decode JSON body.
        var json = fetchUserJson(username);

        if (json == null || json.equals("")) {
            return null;
        }

        var result = new Gson().fromJson(json, MojangUser.class);
        result.id = hyphenateUUID(result.id);

        return result;
    }

    /**
     * Forms the base URL for the Mojang API request.
     * 
     * @param username
     * @return
     */
    private static String formUrl(String username) {
        String baseUrl = "https://api.mojang.com/users/profiles/minecraft/";
        return String.format("%s%s?at=%s", baseUrl, username, System.currentTimeMillis() / 1000L);
    }

    /**
     * Reads all of a request's body and returns a concatenated string of the
     * contents.
     * 
     * @param username
     * @return
     */
    private static String fetchUserJson(String username) {
        try {
            var input = new URL(formUrl(username)).openStream();
            var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                builder.append((char) character);
            }

            input.close();
            reader.close();

            return builder.toString();
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }

    /**
     * Mojang API sends back de-hyphenated UUIDs. This is a util method to add those
     * hyphens back in.
     * 
     * @param uuid
     * @return
     */
    private static String hyphenateUUID(String uuid) {
        return uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5");
    }
}
