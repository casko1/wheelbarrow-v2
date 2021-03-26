package com.casko1.wheelbarrow.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public class DistortionConfig extends FilterConfig {

    private float scale = 1.0f;
    public DistortionPcmAudioFilter distortionPcmAudioFilter;
    private boolean enabled = false;

    public DistortionConfig(){
        super("Distortion");
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void disable(){
        this.scale = 1.0f;
        this.enabled = false;
    }

    public void enable(){
        this.enabled = true;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    public void updateConfig(){
        this.distortionPcmAudioFilter.setScale(this.scale);
    }

    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output){

        this.distortionPcmAudioFilter = new DistortionPcmAudioFilter(output, format.channelCount)
                .setScale(this.scale);

        return this.distortionPcmAudioFilter;
    }

    public boolean parseOption(String option, float factor) {
        if ("scale".equals(option)) {
            setScale(factor);
        } else {
            return false;
        }

        return true;
    }
}
