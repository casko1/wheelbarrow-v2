package com.casko1.wheelbarrow.commands.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public class KaraokeConfig {

    private float level = 1.0f;
    private float monoLevel = 1.0f;
    private KaraokePcmAudioFilter karaokePcmAudioFilter;
    private boolean enabled = false;

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

    public void enable(){
        this.enabled = true;
    }

    public void disable(){
        this.level = 1.0f;
        this.monoLevel = 1.0f;
        this.enabled = false;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    public void updateFilter(){
        this.karaokePcmAudioFilter.setLevel(this.level)
                .setMonoLevel(this.monoLevel);
    }

    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output){

        this.karaokePcmAudioFilter = new KaraokePcmAudioFilter(output, format.channelCount, format.sampleRate)
                .setLevel(this.level)
                .setMonoLevel(this.monoLevel);

        return this.karaokePcmAudioFilter;
    }
}
