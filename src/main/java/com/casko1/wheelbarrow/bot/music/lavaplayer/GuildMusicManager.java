package com.casko1.wheelbarrow.bot.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {
    public AudioPlayer audioPlayer;

    public final TrackScheduler trackScheduler;

    private final AudioPlayerSendHandler sendHandler;

    private final FilterConfiguration filterConfiguration;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        this.audioPlayer = manager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer, guild);

        //add scheduler as a listener to AP
        this.audioPlayer.addListener(this.trackScheduler);
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);

        this.filterConfiguration = new FilterConfiguration();
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }

    public FilterConfiguration getFilterConfiguration() {
        return filterConfiguration;
    }

    // only called when a filter gets enabled
    public void setFilters() {
        this.audioPlayer.setFilterFactory(this.filterConfiguration.createFactory());
    }
}
