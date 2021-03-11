package com.casko1.wheelbarrow.commands.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TimescaleConfig {

    private double speed = 1.0;
    private double pitch = 1.0;
    private double rate = 1.0;
    public TimescalePcmAudioFilter timescalePcmAudioFilter;
    private boolean enabled = false;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void enable(){
        this.enabled = true;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    public void updateFilter(){
        this.timescalePcmAudioFilter.setSpeed(this.speed)
                .setPitch(this.pitch)
                .setRate(this.rate);
    }

    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output){
        this.timescalePcmAudioFilter = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate)
                .setSpeed(this.speed)
                .setPitch(this.pitch)
                .setRate(this.rate);

        return this.timescalePcmAudioFilter;
    }
}
