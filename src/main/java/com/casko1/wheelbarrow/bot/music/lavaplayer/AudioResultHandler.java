package com.casko1.wheelbarrow.bot.music.lavaplayer;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.entities.AdditionalTrackData;
import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.casko1.wheelbarrow.bot.utils.TrackUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final TrackScheduler scheduler;
    private final PlayRequest request;
    private final File defaultImage;
    private final SpotifyApi spotifyApi;
    private final ClientCredentials clientCredentials;

    public AudioResultHandler(PlayRequest request, TrackScheduler scheduler, File defaultImage,
                              SpotifyApi spotifyApi, ClientCredentials clientCredentials) {
        this.scheduler = scheduler;
        this.request = request;
        this.defaultImage = defaultImage;
        this.spotifyApi = spotifyApi;
        this.clientCredentials = clientCredentials;
    }


    //when loading from url
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        AudioTrackInfo audioTrackInfo = audioTrack.getInfo();

        //url provided so we supply with title
        String thumbnail = TrackUtil.getThumbnail(audioTrackInfo.title, spotifyApi, clientCredentials);

        audioTrack.setUserData(new AdditionalTrackData(request.getRequester(),
                thumbnail,
                audioTrackInfo.length,
                defaultImage));

        sendEmbed(audioTrack, request.getEvent());

        scheduler.addToQueue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        if (audioPlaylist.getTracks().isEmpty()) {
            request.getEvent().reply("There was an issue playing that track. ");
            return;
        }

        //search result
        if (audioPlaylist.isSearchResult()) {
            AudioTrack audioTrack = audioPlaylist.getTracks().get(0);

            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            String thumbnail = "attachment";

            if (!request.isPlaylist()) {
                thumbnail = TrackUtil.getThumbnail(request.getImageSearchString(), spotifyApi, clientCredentials);
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
        if (!request.isPlaylist()) {
            request.getEvent().reply("Loading failed.");
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
            //spotify api has found thumbnail
            eb.setThumbnail(addTrackData.getThumbnail());
            event.replyEmbed(eb, null);
        }
    }
}
