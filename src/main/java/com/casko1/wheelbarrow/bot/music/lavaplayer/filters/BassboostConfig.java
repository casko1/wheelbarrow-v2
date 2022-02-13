package com.casko1.wheelbarrow.bot.music.lavaplayer.filters;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BassboostConfig extends FilterConfig {

    private boolean enabled = false;
    public Equalizer equalizer;
    public float[] bandMultipliers;


    public BassboostConfig(){
        super("Bassboost");
        bandMultipliers = new float[15];
    }

    public void setGain(float gain){
        float eGain = Math.min(Math.max(gain, 0), 10) / 10;
        bandMultipliers[0] = (float) 0.3 * eGain;
        bandMultipliers[1] = (float) 0.57 * eGain;
        bandMultipliers[2] = (float) 0.7* eGain;
        bandMultipliers[3] = (float) -0.028 * eGain;
        bandMultipliers[4] = (float) -0.08 * eGain;
        bandMultipliers[5] = (float) -0.04 * eGain;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public List<String> getOptions() {
        List<String> options = new ArrayList<>(Collections.singletonList("gain"));
        if(enabled) options.add("disable");

        return options;
    }

    @Override
    public void updateConfig() {
    }

    @Override
    public boolean parseOption(String option, float factor) {
        if ("gain".equals(option)) {
            setGain(factor);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        this.equalizer = new Equalizer(format.channelCount, output, this.bandMultipliers);
        return this.equalizer;
    }
}
