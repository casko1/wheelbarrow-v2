package com.casko1.wheelbarrow.bot.utils;

import java.io.*;
import java.util.Properties;

public final class PropertiesUtil {

    public static void initPropertiesFile() throws IOException {
        Properties p = new Properties();

        OutputStream os = new FileOutputStream("wheelbarrow.properties", false);

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
    }

    public static Properties getProperties() throws IOException {
        try {
            Properties p = new Properties();
            InputStream configFile = new FileInputStream("wheelbarrow.properties");
            p.load(configFile);
            configFile.close();
            return p;
        } catch (FileNotFoundException e){
            initPropertiesFile();
            return null;
        }
    }

    public static String getProperty(String name) throws IOException {
        Properties p = getProperties();
        return p.getProperty(name);
    }
}
