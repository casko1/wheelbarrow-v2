package com.casko1.wheelbarrow.bot.commands.slash.music;

import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Arrays;

@SuppressWarnings("ConstantConditions")
public class PlayCommand extends SlashCommand {

    public PlayCommand(){
        this.name = "play";
        this.help = "Plays a song or playlist from specified url or query.";
        this.options = Arrays.asList(
                new OptionData(
                        OptionType.STRING, "query", "URL or name of the song/playlist", true
                ),
                new OptionData(
                        OptionType.BOOLEAN, "shuffle", "Shuffle the playlist", false
                )
        );
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();

        TextChannel channel = event.getTextChannel();
        GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()){
            event.reply("You must be in voice channel to use this command.").queue();
            return;
        }

        if(!selfVoiceState.inAudioChannel()){
            joinVoiceChannel(event, memberVoiceState, channel);
        }
        else if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("You must be in the same channel as me to use this command!").queue();
            return;
        }

        parseAndPlay(event, channel, member);
    }

    private void joinVoiceChannel(SlashCommandEvent event, GuildVoiceState memberVoiceState, TextChannel channel){

        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioChannel voiceChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(voiceChannel);

        PlayerManager.getInstance().setTextChannel(event.getGuild(), channel);
    }

    private void parseAndPlay(SlashCommandEvent event, TextChannel channel, Member member){

        String link = event.getOption("query").getAsString();
        boolean shuffle = event.hasOption("shuffle") && event.getOption("shuffle").getAsBoolean();

        if(ArgumentsUtil.isUrl(link)){
            PlayRequest request = new PlayRequest(event, link, "", true, member, shuffle);

            switch(ArgumentsUtil.parseURL(link)){
                case "spotify.com", "open.spotify.com" -> playSpotify(link, request, member, event);
                case "soundcloud.com" -> playSoundcloud(event, link, member);
                case "" -> event.reply("An error occurred. Please try again.").queue();
                default -> PlayerManager.getInstance().loadAndPlay(request);
            }
        }
        else{
            String query = link;
            link = "ytsearch:" + link;
            //false because we only take the first search result
            PlayRequest request = new PlayRequest(event, link, query, false, member, false);

            PlayerManager.getInstance().loadAndPlay(request);
        }
    }

    private void playSpotify(String link, PlayRequest request, Member member, SlashCommandEvent event){
        switch (ArgumentsUtil.parseSpotifyUrl(link)) {
            case "playlist" -> PlayerManager.getInstance().loadSpotifyTracks("playlist", request);
            case "album" -> PlayerManager.getInstance().loadSpotifyTracks("album", request);
            case "track" -> PlayerManager.getInstance().loadSpotifyTrack(event, link, member);
            default -> event.reply("Spotify URL could not be parsed").queue();
        }
    }

    private void playSoundcloud(SlashCommandEvent event, String link, Member member){
        String query = link;
        link = "scsearch:" + link;
        PlayRequest request = new PlayRequest(event, link, query, false, member, false);

        PlayerManager.getInstance().loadAndPlay(request);
    }

}
