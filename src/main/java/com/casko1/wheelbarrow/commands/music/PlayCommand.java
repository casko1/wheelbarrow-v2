package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.entities.PlayRequest;
import com.casko1.wheelbarrow.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.TrackUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
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

        if(!memberVoiceState.inVoiceChannel()){
            event.reply("You must be in voice channel to use this command.");
            return;
        }

        if(!selfVoiceState.inVoiceChannel()){
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
        VoiceChannel voiceChannel = memberVoiceState.getChannel();

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

        if(ArgumentsUtil.isSpotifyURL(link)){
            PlayRequest request = new PlayRequest(channel, link, "", true, member, shuffle);
            switch (ArgumentsUtil.parseSpotifyUrl(link)) {
                case "playlist" -> PlayerManager.getInstance().loadSpotifyTracks("playlist", request);
                case "album" -> PlayerManager.getInstance().loadSpotifyTracks("album", request);
                case "track" -> PlayerManager.getInstance().loadSpotifyTrack(channel, link, member);
                default -> event.reply("Spotify URL could not be parsed");
            }
        }
        else if(!ArgumentsUtil.isUrl(link)){
            String query = link;
            link = "ytsearch:" + link;
            //false because we only take the first search result
            PlayRequest request = new PlayRequest(channel, link, query, false, member, false);

            PlayerManager.getInstance().loadAndPlay(request);
        }
        else{
            //true as it might be a playlist
            PlayRequest request = new PlayRequest(channel, link, "", true, member, shuffle);

            PlayerManager.getInstance().loadAndPlay(request);
        }
    }
}
