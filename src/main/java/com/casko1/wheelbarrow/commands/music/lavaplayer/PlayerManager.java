package com.casko1.wheelbarrow.commands.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    //only one instance
    private static PlayerManager instance;

    //maps guild id's to music managers
    private final Map<Long, GuildMusicManager> musicManagers;
    private final Map<Long, TextChannel> textChannelManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.textChannelManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

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

    //sets the text channel to reply in
    public void setTextChannelManager(Guild guild, TextChannel textChannel){
        Long longId = guild.getIdLong();
        if(!textChannelManagers.containsKey(longId)){
            textChannelManagers.put(longId, textChannel);
        }
    }

    //removes the text channel connected to guild
    public void removeTextChannelManager(Guild guild){
        textChannelManagers.remove(guild.getIdLong());
    }

    //returns the text channel connected with guild
    public TextChannel getTextChannelManager(Guild guild){
        return textChannelManagers.get(guild.getIdLong());
    }

    public void loadAndPLay(TextChannel channel, String trackUrl, boolean isPlaylistLink){
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.trackScheduler.addToQueue(audioTrack);

                channel.sendMessage("Added ")
                        .append(audioTrack.getInfo().title)
                        .append(" by ")
                        .append(audioTrack.getInfo().author)
                        .append(" to the queue.")
                        .queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if(isPlaylistLink){
                    for(AudioTrack audioTrack : audioPlaylist.getTracks()){
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

                    musicManager.trackScheduler.addToQueue(audioTrack);

                    channel.sendMessage("Added ")
                            .append(audioTrack.getInfo().title)
                            .append(" by ")
                            .append(audioTrack.getInfo().author)
                            .append(" to the queue.")
                            .queue();
                }
            }

            @Override
            public void noMatches() {
            }

            @Override
            public void loadFailed(FriendlyException e) {
            }
        });
    }

    public static PlayerManager getInstance(){

        if(instance == null){
            instance = new PlayerManager();
        }

        return instance;
    }
}
