package com.casko1.wheelbarrow.music.lavaplayer;

import com.casko1.wheelbarrow.entities.AdditionalTrackData;
import com.casko1.wheelbarrow.utils.TrackUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ParseException;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    //only one instance
    private static PlayerManager instance;

    //maps guild id's to music managers
    private final Map<Long, GuildMusicManager> musicManagers;
    private final Map<Long, TextChannel> textChannelManagers;
    private final AudioPlayerManager audioPlayerManager;
    private final File defaultImage;
    private final SpotifyApi spotifyApi;
    private ClientCredentials clientCredentials;


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

    public void removeMusicManager(Long guild){
        this.musicManagers.remove(guild);
    }

    //sets the text channel to reply in
    public void setTextChannel(Guild guild, TextChannel textChannel){
        Long longId = guild.getIdLong();
        if(!textChannelManagers.containsKey(longId)){
            textChannelManagers.put(longId, textChannel);
        }
    }

    //removes the text channel connected to guild
    public void removeTextChannel(Guild guild){
        textChannelManagers.remove(guild.getIdLong());
    }

    //returns the text channel connected to guild
    public TextChannel getTextChannel(Guild guild){
        return textChannelManagers.get(guild.getIdLong());
    }

    public void loadAndPlay(TextChannel channel, String trackUrl, boolean isPlaylistLink,
                            boolean isSpotifyPlaylist, Member requester, String... query){
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            //triggered when track is loaded with an URL
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                AudioTrackInfo audioTrackInfo = audioTrack.getInfo();

                //url provided so we supply with title
                String thumbnail = TrackUtil.getThumbnail(audioTrackInfo.title, spotifyApi, clientCredentials);

                audioTrack.setUserData(new AdditionalTrackData(requester,
                        thumbnail,
                        audioTrackInfo.length,
                        defaultImage));

                sendEmbed(audioTrack, channel);

                musicManager.trackScheduler.addToQueue(audioTrack);
            }

            //triggered when track is loaded with query or playlist URL
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if(isPlaylistLink){
                    for(AudioTrack audioTrack : audioPlaylist.getTracks()){
                        AudioTrackInfo audioTrackInfo = audioTrack.getInfo();

                        //loading images for playlist is expensive so we use default image
                        audioTrack.setUserData(new AdditionalTrackData(requester,
                                "attachment",
                                audioTrackInfo.length,
                                defaultImage));


                        musicManager.trackScheduler.addToQueue(audioTrack);
                    }

                    channel.sendMessage("Added ")
                            .append(String.valueOf(audioPlaylist.getTracks().size()))
                            .append(" tracks from playlist ")
                            .append(audioPlaylist.getName())
                            .append(" to the queue.")
                            .queue();
                }
                else{
                    AudioTrack audioTrack = audioPlaylist.getTracks().get(0);

                    AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
                    String thumbnail = "attachment";

                    if(!isSpotifyPlaylist){
                        thumbnail = TrackUtil.getThumbnail(query[0], spotifyApi, clientCredentials);
                    }

                    //loading images for playlist is expensive so we use default image
                    audioTrack.setUserData(new AdditionalTrackData(requester,
                            thumbnail,
                            audioTrackInfo.length,
                            defaultImage));

                    if(!isSpotifyPlaylist){
                        sendEmbed(audioTrack, channel);
                    }

                    musicManager.trackScheduler.addToQueue(audioTrack);
                }
            }

            @Override
            public void noMatches() {
                if(!isPlaylistLink && ! isSpotifyPlaylist){
                    channel.sendMessage("No results with that query were found").queue();
                }
            }

            @Override
            public void loadFailed(FriendlyException e) {
                if(!isPlaylistLink && ! isSpotifyPlaylist){
                    channel.sendMessage("Loading failed.").queue();
                }
            }
        });
    }

    public void loadSpotifyPlaylist(TextChannel channel, String url, Member requester){
        Playlist playlist = TrackUtil.getPlaylist(url, spotifyApi, clientCredentials);
        PlaylistTrack[] tracks = playlist.getTracks().getItems();

        int numberOfTracks = Math.min(tracks.length, 100);

        channel.sendMessage("Added ")
                .append(String.valueOf(numberOfTracks))
                .append(" tracks from playlist ")
                .append(playlist.getName())
                .append(" to the queue.")
                .queue();

        for(int i = 0; i < numberOfTracks; i++){
            loadAndPlay(channel,
                    String.format("ytsearch:%s", TrackUtil.getTitle(tracks[i].getTrack().getId(), spotifyApi, clientCredentials)),
                    false,
                    true,
                    requester);
        }
    }

    public String getSpotifyTitle(String url){
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

    public void sendEmbed(AudioTrack audioTrack, TextChannel channel) {
        final AudioTrackInfo info = audioTrack.getInfo();

        final AdditionalTrackData addTrackData = audioTrack.getUserData(AdditionalTrackData.class);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLUE);

        eb.addField("Adding to queue", String.format("[%s by %s](%s)", info.title, info.author, info.uri), false);
        eb.addField("Duration: ", addTrackData.getDuration(), true);
        eb.addField("Requested by: ",addTrackData.getRequester().getAsMention(), true);


        if(addTrackData.getThumbnail().equals("attachment")){
            //default case

            eb.setThumbnail("attachment://thumbnail.png");
            channel.sendMessage(eb.build()).addFile(defaultImage, "thumbnail.png").queue();
        }
        else{
            //spotify api has found thumbnail
            eb.setThumbnail(addTrackData.getThumbnail());
            channel.sendMessage(eb.build()).queue();
        }
    }

    public static PlayerManager getInstance() {

        if(instance == null){
            try{
                instance = new PlayerManager();
            } catch (IOException | ParseException | SpotifyWebApiException e){
                System.out.println(e);
            }
        }

        return instance;
    }
}
