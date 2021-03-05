package com.casko1.wheelbarrow.utils;

public final class TimeConverterUtil {

    public static String getMinutesAndSeconds(long timestamp){

        long minutes = (timestamp / 1000) / 60;
        int seconds = (int)((timestamp / 1000) % 60);

        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}
