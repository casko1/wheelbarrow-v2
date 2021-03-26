package com.casko1.wheelbarrow.utils;

import com.casko1.wheelbarrow.music.lavaplayer.PlayerManager;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

/*
    this util is used by numerous commands for checking
    if the command may be executed
 */
@SuppressWarnings("ConstantConditions")
public final class VoiceStateCheckUtil {

    public static boolean isEligible(CommandEvent event){

        AudioTrack audioTrack = PlayerManager
                .getInstance()
                .getMusicManager(event.getGuild())
                .audioPlayer
                .getPlayingTrack();

        Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inVoiceChannel()){
            event.reply("You must be in voice channel to use this command.");
            return false;
        }

        if(!selfVoiceState.inVoiceChannel()){
            event.reply("I am not currently in a voice channel!");
            return false;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("You must be in the same channel as me to use this command!");
            return false;
        }

        if(audioTrack == null){
            event.reply("Nothing is playing right now.");
            return false;
        }

        return true;
    }
}
