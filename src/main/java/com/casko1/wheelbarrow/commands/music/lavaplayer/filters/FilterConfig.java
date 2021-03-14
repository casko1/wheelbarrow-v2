package com.casko1.wheelbarrow.commands.music.lavaplayer.filters;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public interface FilterConfig {

    void enable();

    void disable();

    boolean isEnabled();

    String getName();

    AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output);

}
