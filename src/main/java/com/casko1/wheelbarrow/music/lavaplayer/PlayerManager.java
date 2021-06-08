package com.casko1.wheelbarrow.music.lavaplayer;

import com.casko1.wheelbarrow.entities.PlayRequest;
import com.casko1.wheelbarrow.utils.TrackUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        List<String> config = Files.readAllLines(Paths.get("config.txt"));
        String spotifyId = config.get(3);
        String spotifySecret = config.get(4);

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

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
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
        final GuildMusicManager musicManager = this.getMusicManager(request.getTextChannel().getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, request.getSearchString(),
                new AudioResultHandler(request, musicManager.trackScheduler, defaultImage, spotifyApi, clientCredentials));
    }

    public void loadSpotifyPlaylist(TextChannel channel, String url, Member requester, boolean shuffle) {
        Playlist playlist = TrackUtil.getPlaylist(url, spotifyApi, clientCredentials);

        if (playlist == null) {
            channel.sendMessage("An error occurred while loading the playlist.").queue();
            return;
        }

        List<PlaylistTrack> tracks = Arrays.asList(playlist.getTracks().getItems());

        if (shuffle) Collections.shuffle(tracks);

        int numberOfTracks = Math.min(tracks.size(), 100);

        channel.sendMessage("Added ")
                .append(String.valueOf(numberOfTracks))
                .append(" tracks from playlist ")
                .append(playlist.getName())
                .append(" to the queue.")
                .queue();

        for (int i = 0; i < numberOfTracks; i++) {
            PlayRequest request = new PlayRequest(channel,
                    String.format("ytsearch:%s", getSpotifyTitle(tracks.get(i).getTrack().getId())),
                    "",
                    true,
                    requester,
                    shuffle);

            loadAndPlay(request);
        }
    }

    public void loadSpotifyAlbum(TextChannel channel, String url, Member requester, boolean shuffle) {
        Album album = TrackUtil.getAlbum(url, spotifyApi, clientCredentials);

        if (album == null) {
            channel.sendMessage("An error occurred while loading the album.").queue();
            return;
        }

        List<TrackSimplified> tracks = Arrays.asList(album.getTracks().getItems());

        if (shuffle) Collections.shuffle(tracks);

        int numberOfTracks = Math.min(tracks.size(), 100);

        channel.sendMessage("Added ")
                .append(String.valueOf(numberOfTracks))
                .append(" tracks from playlist ")
                .append(album.getName())
                .append(" to the queue.")
                .queue();

        for (int i = 0; i < numberOfTracks; i++) {
            PlayRequest request = new PlayRequest(channel,
                    String.format("ytsearch:%s", getSpotifyTitle(tracks.get(i).getId())),
                    "",
                    true,
                    requester,
                    shuffle);

            loadAndPlay(request);
        }
    }

    public void loadSpotifyTrack(TextChannel channel, String url, Member requester) {
        String title = getSpotifyTitle(url);
        String link = "ytsearch:" + title;
        PlayRequest request = new PlayRequest(channel, link, title, false, requester, false);
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
