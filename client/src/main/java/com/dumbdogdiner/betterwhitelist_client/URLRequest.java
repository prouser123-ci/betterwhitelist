package com.dumbdogdiner.betterwhitelist_client;

import java.net.HttpURLConnection;
import java.net.URL;

public class URLRequest {
    public static int checkUsername(String username) throws Exception {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username + "?at="
                + System.currentTimeMillis() / 1000L);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int code = connection.getResponseCode();

        return code;
    }
}
