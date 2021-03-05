package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.entities.AdditionalTrackData;
import com.casko1.wheelbarrow.utils.TimeConverterUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.File;

@SuppressWarnings("ConstantConditions")
public class NowPlayingCommand extends Command {

    public NowPlayingCommand(){
        this.name = "nowplaying";
        this.help = "Displays information about current track.";
        this.guildOnly = false;
        this.aliases = new String[]{"np"};
    }

    @Override
    protected void execute(CommandEvent event) {
        Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inVoiceChannel()){
            event.reply("You must be in voice channel to use this command.");
            return;
        }

        if(!selfVoiceState.inVoiceChannel()){
            event.reply("I am not currently in a voice channel!");
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("You must be in the same channel as me to use this command!");
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        final AudioTrack audioTrack = audioPlayer.getPlayingTrack();

        if(audioTrack == null){
            event.reply("Nothing is playing right now.");
            return;
        }

        final AudioTrackInfo info = audioTrack.getInfo();

        final AdditionalTrackData addTrackData = audioTrack.getUserData(AdditionalTrackData.class);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLUE);

        eb.addField("Now playing", String.format("[%s by %s](%s)", info.title, info.author, info.uri), false);

        String currentTime = TimeConverterUtil.getMinutesAndSeconds(audioTrack.getPosition());
        eb.addField("Currently at:", String.format("%s of %s", currentTime, addTrackData.getDuration()), true);

        eb.addField("Requested by: ", addTrackData.getRequester().getAsMention(), true);


        if(addTrackData.getThumbnail().equals("attachment")){
            //default case
            File file = new File("src/main/resources/img/default.png");
            eb.setThumbnail("attachment://thumbnail.png");
            event.getTextChannel().sendMessage(eb.build()).addFile(file, "thumbnail.png").queue();
        }
        else{
            //spotify api has found thumbnail
            eb.setThumbnail(addTrackData.getThumbnail());
            event.getTextChannel().sendMessage(eb.build()).queue();
        }

    }
}
