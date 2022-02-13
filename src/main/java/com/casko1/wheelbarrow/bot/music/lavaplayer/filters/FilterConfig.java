package com.casko1.wheelbarrow.bot.music.lavaplayer.filters;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

import java.util.List;

public abstract class FilterConfig {

    private final String name;

    public FilterConfig(String name) {
        this.name = name;
    }

    public abstract void enable();

    public abstract void disable();

    public abstract boolean isEnabled();

    public abstract List<String> getOptions();

    public String getName(){
        return name;
    }

    public abstract void updateConfig();

    public abstract boolean parseOption(String setting, float factor);

    public abstract AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output);

    public boolean applyConfig(String type, Float factor){
        if(!isEnabled()){
            enable();

            if(!parseOption(type, factor)){
                disable();
                return false;
            }
        }
        else{
            if(!parseOption(type, factor)){
                return false;
            }

            updateConfig();
        }

        return true;
    }

}
