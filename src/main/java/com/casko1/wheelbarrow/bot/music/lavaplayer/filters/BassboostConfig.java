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


    public BassboostConfig() {
        super("bassboost");
        bandMultipliers = new float[15];
    }

    public void setGain(float gain) {
        float eGain = Math.min(Math.max(gain, 0), 10) / 10;
        bandMultipliers[0] = 0.2f * eGain;
        bandMultipliers[1] = 0.15f * eGain;
        bandMultipliers[2] = 0.1f * eGain;
        bandMultipliers[3] = 0.05f * eGain;
        bandMultipliers[4] = 0.0f * eGain;
        bandMultipliers[5] = -0.05f * eGain;
        bandMultipliers[6] = -0.1f * eGain;
        bandMultipliers[7] = -0.1f * eGain;
        bandMultipliers[8] = -0.1f * eGain;
        bandMultipliers[9] = -0.1f * eGain;
        bandMultipliers[10] = -0.1f * eGain;
        bandMultipliers[11] = -0.1f * eGain;
        bandMultipliers[12] = -0.1f * eGain;
        bandMultipliers[13] = -0.1f * eGain;
        bandMultipliers[14] = -0.1f * eGain;
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
        return new ArrayList<>(Collections.singletonList("gain"));
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
