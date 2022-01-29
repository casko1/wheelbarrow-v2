package com.casko1.wheelbarrow.bot.commands.message.music;

import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class PlayCommand extends Command {

    public PlayCommand(){
        this.name = "play";
        this.help = "Plays a song or playlist from specified url or query." +
                " Optional flags can be added at the end of the command.";
        this.arguments = "[-s; shuffles the playlist]";
        this.aliases = new String[]{"p"};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(event.getArgs().isBlank()){
            event.reply("You need to provide link or a query.");
            return;
        }

        TextChannel channel = event.getTextChannel();
        Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()){
            event.reply("You must be in voice channel to use this command.");
            return;
        }

        if(!selfVoiceState.inAudioChannel()){
            joinVoiceChannel(event, memberVoiceState, channel);
        }
        else if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("You must be in the same channel as me to use this command!");
            return;
        }

        parseAndPlay(event, channel, member);

    }

    private void joinVoiceChannel(CommandEvent event, GuildVoiceState memberVoiceState, TextChannel channel){

        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioChannel voiceChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(voiceChannel);
        event.replyFormatted("Joining %s.", voiceChannel.getName());

        PlayerManager.getInstance().setTextChannel(event.getGuild(), channel);
    }

    private void parseAndPlay(CommandEvent event, TextChannel channel, Member member){

        boolean shuffle = false;

        List<String> split = new ArrayList<>(Arrays.asList(event.getArgs().split("\\s+")));

        if(split.get(split.size() - 1).equals("-s")){
            shuffle = true;
            split.remove(split.size() - 1);
        }

        String link = String.join("\\s+", split);

        if(ArgumentsUtil.isUrl(link)){
            PlayRequest request = new PlayRequest(channel, link, "", true, member, shuffle);

            switch(ArgumentsUtil.parseURL(link)){
                case "spotify.com", "open.spotify.com" -> playSpotify(channel, link, request, member, event);
                case "soundcloud.com" -> playSoundcloud(channel, link, member);
                case "" -> event.reply("An error occurred. Please try again.");
                default -> PlayerManager.getInstance().loadAndPlay(request);
            }
        }
        else{
            String query = link;
            link = "ytsearch:" + link;
            //false because we only take the first search result
            PlayRequest request = new PlayRequest(channel, link, query, false, member, false);

            PlayerManager.getInstance().loadAndPlay(request);
        }
    }

    private void playSpotify(TextChannel channel, String link, PlayRequest request, Member member, CommandEvent event){
        switch (ArgumentsUtil.parseSpotifyUrl(link)) {
            case "playlist" -> PlayerManager.getInstance().loadSpotifyTracks("playlist", request);
            case "album" -> PlayerManager.getInstance().loadSpotifyTracks("album", request);
            case "track" -> PlayerManager.getInstance().loadSpotifyTrack(channel, link, member);
            default -> event.reply("Spotify URL could not be parsed");
        }
    }

    private void playSoundcloud(TextChannel channel, String link, Member member){
        String query = link;
        link = "scsearch:" + link;
        PlayRequest request = new PlayRequest(channel, link, query, false, member, false);

        PlayerManager.getInstance().loadAndPlay(request);
    }

}
