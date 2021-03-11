package com.casko1.wheelbarrow.commands.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    private final Guild guild;

    public TrackScheduler(AudioPlayer player, Guild guild){
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

        if(endReason.mayStartNext){
            nextTrack();
        }
    }

    public void addToQueue(AudioTrack track){
        //if a track is already playing it adds track to the queue
        //in that case startTrack return false
        if(!this.player.startTrack(track, true)){
            this.queue.offer(track);
        }
    }

    public void nextTrack(){
        AudioTrack track = this.queue.poll();
        if(track == null){
            this.player.stopTrack();
            PlayerManager playerManager = PlayerManager.getInstance();
            TextChannel textChannel = playerManager.getTextChannel(guild);
            textChannel.sendMessage("Nothing left to play. Leaving the voice channel.").queue();
            playerManager.removeTextChannel(guild);
            guild.getAudioManager().closeAudioConnection();
            playerManager.removeMusicManager(guild.getIdLong());
        }
        else{
            this.player.startTrack(track, false);
        }

    }
}
