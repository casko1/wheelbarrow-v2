package com.casko1.wheelbarrow.commands.music.lavaplayer;

import com.casko1.wheelbarrow.commands.music.lavaplayer.filters.DistortionConfig;
import com.casko1.wheelbarrow.commands.music.lavaplayer.filters.KaraokeConfig;
import com.casko1.wheelbarrow.commands.music.lavaplayer.filters.TimescaleConfig;
import com.casko1.wheelbarrow.commands.music.lavaplayer.filters.TremoloConfig;
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

            List<AudioFilter> filterChain = new ArrayList<>();

            filterChain.add(output);

            if(filterConfiguration.timescale.isEnabled()){
                filterChain.add(0, filterConfiguration.timescale.create(audioDataFormat, (FloatPcmAudioFilter) filterChain.get(0)));
            }

            if(filterConfiguration.karaoke.isEnabled()){
                filterChain.add(0, filterConfiguration.karaoke.create(audioDataFormat, (FloatPcmAudioFilter) filterChain.get(0)));
            }

            if(filterConfiguration.distortion.isEnabled()){
                filterChain.add(0, filterConfiguration.distortion.create(audioDataFormat,(FloatPcmAudioFilter) filterChain.get(0)));
            }

            if(filterConfiguration.tremolo.isEnabled()){
                filterChain.add(0, filterConfiguration.tremolo.create(audioDataFormat,(FloatPcmAudioFilter) filterChain.get(0)));
            }

            return filterChain.subList(0, filterChain.size()-1);
        }
    }
}
