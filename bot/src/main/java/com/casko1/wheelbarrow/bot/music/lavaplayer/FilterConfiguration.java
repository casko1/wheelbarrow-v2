package com.casko1.wheelbarrow.bot.music.lavaplayer;

import com.casko1.wheelbarrow.bot.music.lavaplayer.filters.*;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class FilterConfiguration {

    public TimescaleConfig timescale = new TimescaleConfig();
    public KaraokeConfig karaoke = new KaraokeConfig();
    public DistortionConfig distortion = new DistortionConfig();
    public TremoloConfig tremolo = new TremoloConfig();
    public RotationConfig rotation = new RotationConfig();
    public BassboostConfig bassboost = new BassboostConfig();
    public List<FilterConfig> filterConfigs = new ArrayList<>();

    public FilterConfiguration(){
        filterConfigs.add(timescale);
        filterConfigs.add(karaoke);
        filterConfigs.add(distortion);
        filterConfigs.add(tremolo);
        filterConfigs.add(rotation);
        filterConfigs.add(bassboost);
    }

    private List<FilterConfig> getConfigs(){
        return this.filterConfigs;
    }

    public PcmFilterFactory createFactory(){
        return new Factory(this);
    }

    private static class Factory implements PcmFilterFactory {

        private final FilterConfiguration filterConfiguration;

        private Factory(FilterConfiguration filterConfiguration){
            this.filterConfiguration = filterConfiguration;
        }

        //gets called when a new filter gets enabled
        @Override
        public List<AudioFilter> buildChain(AudioTrack audioTrack, AudioDataFormat audioDataFormat, UniversalPcmAudioFilter output) {

            List<FilterConfig> filterConfigs = filterConfiguration.getConfigs();

            List<AudioFilter> filterChain = new ArrayList<>();

            filterChain.add(output);

            for(FilterConfig config : filterConfigs){
                if(config.isEnabled()){
                    filterChain.add(0, config.create(audioDataFormat, (FloatPcmAudioFilter) filterChain.get(0)));
                }
            }

            return filterChain.subList(0, filterChain.size()-1);
        }
    }
}
