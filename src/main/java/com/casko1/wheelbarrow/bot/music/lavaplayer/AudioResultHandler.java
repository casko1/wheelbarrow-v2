package com.casko1.wheelbarrow.bot.music.lavaplayer;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.entities.AdditionalTrackData;
import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class AudioResultHandler implements AudioLoadResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(AudioResultHandler.class);

    private final TrackScheduler scheduler;
    private final PlayRequest request;
    private final File defaultImage;

    public AudioResultHandler(PlayRequest request, TrackScheduler scheduler, File defaultImage) {
        this.scheduler = scheduler;
        this.request = request;
        this.defaultImage = defaultImage;
    }


    //when loading from url
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        logger.info("Track loaded");
        AudioTrackInfo audioTrackInfo = audioTrack.getInfo();

        //url provided so we supply with title
        String thumbnail = audioTrackInfo.artworkUrl;

        audioTrack.setUserData(new AdditionalTrackData(request.getRequester(),
                thumbnail,
                audioTrackInfo.length,
                defaultImage));

        sendEmbed(audioTrack, request.getEvent());

        scheduler.addToQueue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        logger.info("Playlist loaded");
        if (audioPlaylist.getTracks().isEmpty()) {
            request.getEvent().reply("There was an issue playing that track");
            return;
        }

        //search result
        if (audioPlaylist.isSearchResult()) {
            AudioTrack audioTrack = audioPlaylist.getTracks().getFirst();

            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            String thumbnail = "attachment";

            if (!request.isPlaylist()) {
                thumbnail = audioTrackInfo.artworkUrl;
            }

            audioTrack.setUserData(new AdditionalTrackData(request.getRequester(),
                    thumbnail,
                    audioTrackInfo.length,
                    defaultImage));

            if (!request.isPlaylist()) {
                sendEmbed(audioTrack, request.getEvent());
            }

            scheduler.addToQueue(audioTrack);
        } else {
            List<AudioTrack> tracks = audioPlaylist.getTracks();

            if (request.isShuffle()) Collections.shuffle(tracks);

            for (AudioTrack audioTrack : tracks) {
                AudioTrackInfo audioTrackInfo = audioTrack.getInfo();

                //loading images for playlist is expensive, so we use default image
                audioTrack.setUserData(new AdditionalTrackData(request.getRequester(),
                        "attachment",
                        audioTrackInfo.length,
                        defaultImage));


                scheduler.addToQueue(audioTrack);
            }

            String out = "Added" +
                    audioPlaylist.getTracks().size() +
                    " tracks from playlist " +
                    audioPlaylist.getName() +
                    " to the queue.";

            request.getEvent().reply(out);
        }
    }

    @Override
    public void noMatches() {
        if (!request.isPlaylist()) {
            request.getEvent().reply("No results with that query were found");
        }
    }

    @Override
    public void loadFailed(FriendlyException e) {
        logger.error("Loading track failed: {}", e.toString());

        if (!request.isPlaylist()) {
            request.getEvent().reply("Loading failed");
        }
    }

    public void sendEmbed(AudioTrack audioTrack, PlayEvent event) {
        final AudioTrackInfo info = audioTrack.getInfo();

        final AdditionalTrackData addTrackData = audioTrack.getUserData(AdditionalTrackData.class);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLUE);

        eb.addField("Adding to queue", String.format("[%s by %s](%s)", info.title, info.author, info.uri), false);
        eb.addField("Duration: ", addTrackData.getDuration(), true);
        eb.addField("Requested by: ", addTrackData.getRequester().getAsMention(), true);


        if (addTrackData.getThumbnail().equals("attachment")) {
            //default case
            eb.setThumbnail("attachment://thumbnail.png");
            event.replyEmbed(eb, defaultImage);
        } else {
            //thumbnail could not be found
            eb.setThumbnail(addTrackData.getThumbnail());
            event.replyEmbed(eb);
        }
    }
}
