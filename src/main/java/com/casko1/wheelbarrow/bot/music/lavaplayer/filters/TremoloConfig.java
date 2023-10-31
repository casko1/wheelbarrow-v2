package com.casko1.wheelbarrow.bot.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.tremolo.TremoloPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TremoloConfig extends FilterConfig {

    private float frequency = 2.0f;
    private float depth = 0.5f;
    private TremoloPcmAudioFilter tremoloPcmAudioFilter;
    private boolean enabled = false;

    public TremoloConfig() {
        super("tremolo");
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public void disable() {
        this.frequency = 2.0f;
        this.depth = 0.5f;
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<String> getOptions() {
        return new ArrayList<>(Arrays.asList("depth", "freq"));
    }

    public void updateConfig() {
        this.tremoloPcmAudioFilter.setDepth(this.depth)
                .setFrequency(this.frequency);
    }

    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {

        this.tremoloPcmAudioFilter = new TremoloPcmAudioFilter(output, format.channelCount, format.sampleRate)
                .setDepth(this.depth)
                .setFrequency(this.frequency);

        return this.tremoloPcmAudioFilter;
    }

    public boolean parseOption(String setting, float factor) {
        switch (setting) {
            case "depth" -> setDepth(factor);
            case "freq" -> setFrequency(factor);
            default -> {
                return false;
            }
        }

        return true;
    }
}
