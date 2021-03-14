package com.casko1.wheelbarrow.commands.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public class DistortionConfig implements FilterConfig {

    private float scale = 1.0f;
    public DistortionPcmAudioFilter distortionPcmAudioFilter;
    public boolean enabled = false;

    public String getName(){
        return "Distortion";
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void disableFilter(){
        this.scale = 1.0f;
        this.disable();
    }

    public void enable(){
        this.enabled = true;
    }

    public void disable(){
        this.enabled = false;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    public void updateFilter(){
        this.distortionPcmAudioFilter.setScale(this.scale);
    }

    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output){

        this.distortionPcmAudioFilter = new DistortionPcmAudioFilter(output, format.channelCount)
                .setScale(this.scale);

        return this.distortionPcmAudioFilter;
    }
}
