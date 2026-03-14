package com.casko1.wheelbarrow.bot.music.lavaplayer;

import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ParseException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    //only one instance
    private static PlayerManager instance;

    //maps guild id's to music managers
    private final Map<Long, GuildMusicManager> musicManagers;
    private final Map<Long, TextChannel> textChannelManagers;
    private final AudioPlayerManager audioPlayerManager;
    private final File defaultImage;


    public PlayerManager() throws IOException, ParseException {
        this.musicManagers = new HashMap<>();
        this.textChannelManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        this.audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);

        defaultImage = prepareDefaultImage();

        YoutubeAudioSourceManager youtubeSourceManager = new YoutubeAudioSourceManager(
                true,
                new MusicWithThumbnail(),
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
                new AudioResultHandler(request, musicManager.trackScheduler, defaultImage));
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
            } catch (IOException | ParseException e) {
                System.out.println(e);
            }
        }

        return instance;
    }
}
