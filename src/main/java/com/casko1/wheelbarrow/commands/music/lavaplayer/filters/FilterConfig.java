package com.casko1.wheelbarrow.commands.music.lavaplayer.filters;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public abstract class FilterConfig {

    private final String name;

    public FilterConfig(String name) {
        this.name = name;
    }

    public abstract void enable();

    public abstract void disable();

    public abstract boolean isEnabled();

    public String getName(){
        return name;
    }

    public abstract AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output);

}
