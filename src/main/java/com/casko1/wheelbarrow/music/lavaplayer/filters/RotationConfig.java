package com.casko1.wheelbarrow.music.lavaplayer.filters;

import com.github.natanbc.lavadsp.rotation.RotationPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public class RotationConfig extends FilterConfig{

    private float rotationSpeed = 5.0f;
    private boolean enabled = false;
    public RotationPcmAudioFilter rotationPcmAudioFilter;

    public RotationConfig(){
        super("Rotation");
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.rotationSpeed = 5.0f;
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public float getRotationSpeed(){
        return this.rotationSpeed;
    }

    public void setRotationSpeed(float speed) {
        this.rotationSpeed = speed;
    }

    public void updateConfig() {
        this.rotationPcmAudioFilter.setRotationSpeed(this.rotationSpeed);
    }

    public boolean parseOption(String setting, float factor) {
        if ("speed".equals(setting)) {
            setRotationSpeed(factor);
        } else {
            return false;
        }

        return true;
    }

    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        this.rotationPcmAudioFilter = new RotationPcmAudioFilter(output, format.sampleRate)
                .setRotationSpeed(this.rotationSpeed);

        return this.rotationPcmAudioFilter;
    }

}
