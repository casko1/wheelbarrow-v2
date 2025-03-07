package com.casko1.wheelbarrow.bot.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);

    public final AudioPlayer player;
    public BlockingQueue<AudioTrack> queue;
    private final Guild guild;
    private boolean loop = false;
    private long lastExceptionTimestamp = 0L;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (this.loop) {
                this.player.startTrack(track.makeClone(), false);
                return;
            }

            nextTrack();
        }
    }

    public void shuffle() {
        List<AudioTrack> list = new ArrayList<>(queue);

        Collections.shuffle(list);

        queue = new LinkedBlockingQueue<>(list);
    }

    public void remove(int position) {
        Iterator<AudioTrack> iterator = queue.iterator();

        int index = 1;

        while (iterator.hasNext()) {
            iterator.next();
            if (index == position) {
                iterator.remove();
                break;
            }

            index++;
        }
    }

    public boolean seek(long timestamp) {
        AudioTrack current = player.getPlayingTrack();

        if (current.isSeekable()) {
            current.setPosition(timestamp);
            return true;
        } else {
            return false;
        }
    }

    public void addToQueue(AudioTrack track) {
        //if a track is already playing it adds track to the queue
        //in that case startTrack return false
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void toggleLoop() {
        loop = !loop;
    }

    public boolean isLoop() {
        return loop;
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        logger.error("An error occurred while playing the track: {}", exception.toString());
        long currentTime = System.currentTimeMillis();
        // Throttle to 1 track exception message per 10 seconds in case of a broken playlist
        if (currentTime - lastExceptionTimestamp > 10000L) {
            lastExceptionTimestamp = currentTime;
            PlayerManager playerManager = PlayerManager.getInstance();
            TextChannel textChannel = playerManager.getTextChannel(guild);
            textChannel.sendMessage("There was an error playing that track").queue();
        }
    }

    public void nextTrack() {
        AudioTrack track = this.queue.poll();
        if (track == null) {
            this.player.stopTrack();
            PlayerManager playerManager = PlayerManager.getInstance();
            TextChannel textChannel = playerManager.getTextChannel(guild);
            textChannel.sendMessage("Nothing left to play. Leaving the voice channel").queue();
            playerManager.removeTextChannel(guild);
            guild.getAudioManager().closeAudioConnection();
            playerManager.removeMusicManager(guild.getIdLong());
        } else {
            this.player.startTrack(track, false);
        }

    }
}
