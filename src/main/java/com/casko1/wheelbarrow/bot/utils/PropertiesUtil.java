package com.casko1.wheelbarrow.bot.utils;

import java.io.*;
import java.util.Properties;

public final class PropertiesUtil {

    public static void initPropertiesFile() {
        Properties p = new Properties();

        OutputStream os;

        try {
            os = new FileOutputStream("wheelbarrow.properties", false);

            p.setProperty("botToken", "replaceWithBotToken");
            p.setProperty("ownerId", "replaceWithOwnerId_18numbers");
            p.setProperty("enableWeather", "false");
            p.setProperty("weatherToken", "replaceWithOpenWeatherToken");
            p.setProperty("enableSpotifyThumbnails", "false");
            p.setProperty("spotifyId", "replaceWithSpotifyId");
            p.setProperty("spotifySecret", "replaceWithSpotifySecret");
            p.setProperty("enableSongDetection", "false");
            p.setProperty("shazamCoreApiKey", "replaceShazamCoreApiKey");
            p.setProperty("enableApi", "false");

            p.store(os, "Automatically generated properties file. For basic functionality " +
                    "only botToken is required");

            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties loadProperties() {
        try {
            Properties p = new Properties();
            InputStream configFile = new FileInputStream("wheelbarrow.properties");
            p.load(configFile);
            configFile.close();
            return p;
        } catch (IOException e) {
            initPropertiesFile();
            return null;
        }
    }

    private static final class InstanceHolder {
        private static final Properties instance = loadProperties();
    }

    public static Properties getInstance() throws IOException {
        return InstanceHolder.instance;
    }
}
