package com.casko1.wheelbarrow.bot.music.lavaplayer;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.casko1.wheelbarrow.bot.utils.TrackUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import dev.lavalink.youtube.clients.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ParseException;

import java.io.*;
import java.util.*;

public class PlayerManager {
    //only one instance
    private static PlayerManager instance;

    //maps guild id's to music managers
    private final Map<Long, GuildMusicManager> musicManagers;
    private final Map<Long, TextChannel> textChannelManagers;
    private final AudioPlayerManager audioPlayerManager;
    private final File defaultImage;
    private final SpotifyApi spotifyApi;
    private final ClientCredentials clientCredentials;


    public PlayerManager() throws IOException, ParseException, SpotifyWebApiException {
        String spotifyId = System.getenv("spotifyId");
        String spotifySecret = System.getenv("spotifySecret");

        this.musicManagers = new HashMap<>();
        this.textChannelManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        this.audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);

        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyId)
                .setClientSecret(spotifySecret)
                .build();

        defaultImage = prepareDefaultImage();

        this.clientCredentials = spotifyApi.clientCredentials().build().execute();

        this.spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        YoutubeAudioSourceManager youtubeSourceManager = new YoutubeAudioSourceManager(
                true,
                new MusicWithThumbnail(),
                new TvHtml5EmbeddedWithThumbnail(),
                new MWebWithThumbnail(),
                new WebEmbeddedWithThumbnail(),
                new AndroidVrWithThumbnail(),
                new IosWithThumbnail(),
                new AndroidMusicWithThumbnail()
                );
        this.audioPlayerManager.registerSourceManager(youtubeSourceManager);
        this.audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void removeMusicManager(Long guild) {
        this.musicManagers.remove(guild);
    }

    //sets the text channel to reply in
    public void setTextChannel(Guild guild, TextChannel textChannel) {
        Long longId = guild.getIdLong();
        if (!textChannelManagers.containsKey(longId)) {
            textChannelManagers.put(longId, textChannel);
        }
    }

    //removes the text channel connected to guild
    public void removeTextChannel(Guild guild) {
        textChannelManagers.remove(guild.getIdLong());
    }

    //returns the text channel connected to guild
    public TextChannel getTextChannel(Guild guild) {
        return textChannelManagers.get(guild.getIdLong());
    }

    public void loadAndPlay(PlayRequest request) {
        final GuildMusicManager musicManager = this.getMusicManager(request.getEvent().getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, request.getSearchString(),
                new AudioResultHandler(request, musicManager.trackScheduler, defaultImage, spotifyApi, clientCredentials));
    }

    public void loadSpotifyTracks(String type, PlayRequest request) {
        List<String> trackIds;

        switch (type) {
            case "playlist" ->
                    trackIds = TrackUtil.getPlaylist(request.getSearchString(), spotifyApi, clientCredentials, request.isShuffle());
            case "album" -> trackIds = TrackUtil.getAlbum(request.getSearchString(), spotifyApi, clientCredentials);
            default -> {
                request.getEvent().reply("Could not process your request");
                return;
            }
        }

        if (trackIds == null) {
            request.getEvent().reply("An error occurred while loading the tracks");
            return;
        }

        int trackCount = Math.min(trackIds.size(), 100);

        request.getEvent().reply(String.format("Added %d tracks to the queue", trackCount));

        if (request.isShuffle()) Collections.shuffle(trackIds);

        for (int i = 0; i < trackCount; i++) {
            PlayRequest trackRequest = new PlayRequest(request.getEvent(),
                    String.format("ytsearch:%s", getSpotifyTitle(trackIds.get(i))),
                    "",
                    true,
                    request.getRequester(),
                    false);

            loadAndPlay(trackRequest);
        }
    }

    public void loadSpotifyTrack(PlayEvent event, String url, Member requester) {
        String title = getSpotifyTitle(url);
        String link = "ytsearch:" + title;
        PlayRequest request = new PlayRequest(event, link, title, false, requester, false);
        loadAndPlay(request);
    }

    public String getSpotifyTitle(String url) {
        return TrackUtil.getTitle(url, spotifyApi, clientCredentials);
    }

    private File prepareDefaultImage() throws IOException {
        InputStream in = getClass().getResourceAsStream("/img/default.png");
        byte[] buffer = IOUtils.toByteArray(in);

        File tmp = File.createTempFile("temp", null);
        OutputStream outStream = new FileOutputStream(tmp);
        outStream.write(buffer);
        tmp.deleteOnExit();

        return tmp;
    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            try {
                instance = new PlayerManager();
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                System.out.println(e);
            }
        }

        return instance;
    }
}
