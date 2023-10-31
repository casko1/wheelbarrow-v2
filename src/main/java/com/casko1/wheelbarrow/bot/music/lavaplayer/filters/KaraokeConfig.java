package com.casko1.wheelbarrow.bot.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KaraokeConfig extends FilterConfig {

    private float level = 1.0f;
    private float monoLevel = 1.0f;
    private KaraokePcmAudioFilter karaokePcmAudioFilter;
    private boolean enabled = false;

    public KaraokeConfig() {
        super("karaoke");
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public double getMonoLevel() {
        return monoLevel;
    }

    public void setMonoLevel(float monoLevel) {
        this.monoLevel = monoLevel;
    }

    public void disable() {
        this.level = 1.0f;
        this.monoLevel = 1.0f;
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<String> getOptions() {
        return new ArrayList<>(Arrays.asList("mono", "level"));
    }

    public void updateConfig() {
        this.karaokePcmAudioFilter.setLevel(this.level)
                .setMonoLevel(this.monoLevel);
    }

    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {

        this.karaokePcmAudioFilter = new KaraokePcmAudioFilter(output, format.channelCount, format.sampleRate)
                .setLevel(this.level)
                .setMonoLevel(this.monoLevel);

        return this.karaokePcmAudioFilter;
    }

    public boolean parseOption(String option, float factor) {
        switch (option) {
            case "mono" -> setMonoLevel(factor);
            case "level" -> setLevel(factor);
            default -> {
                return false;
            }
        }

        return true;
    }
}
