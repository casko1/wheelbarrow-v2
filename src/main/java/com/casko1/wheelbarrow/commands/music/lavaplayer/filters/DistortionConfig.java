package com.casko1.wheelbarrow.commands.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class DistortionConfig {

    private float scale = 1.0f;
    public DistortionPcmAudioFilter distortionPcmAudioFilter;
    private boolean enabled = false;

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void enable(){
        this.enabled = true;
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
