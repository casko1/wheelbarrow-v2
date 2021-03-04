package com.casko1.wheelbarrow.commands.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {
    public final AudioPlayer audioPlayer;

    public final TrackScheduler trackScheduler;

    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild){
        this.audioPlayer = manager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer, guild);
        //add scheduler as a listener to AP
        this.audioPlayer.addListener(this.trackScheduler);
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
}
