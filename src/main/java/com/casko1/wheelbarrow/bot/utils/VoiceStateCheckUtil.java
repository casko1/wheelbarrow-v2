package com.casko1.wheelbarrow.bot.utils;

import com.casko1.wheelbarrow.bot.commands.events.CommonSlashCommandEvent;
import com.casko1.wheelbarrow.bot.commands.events.CommonTextCommandEvent;
import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

/**
 * This utility is used by numerous commands for checking if the command may be executed
 */

@SuppressWarnings("ConstantConditions")
public final class VoiceStateCheckUtil {

    //override is used for stop and skip command in case audio track somehow ends up null
    public static boolean isEligible(CommandEvent event, boolean override) {
        return isEligible(new CommonTextCommandEvent(event), override);
    }

    public static boolean isEligible(SlashCommandEvent event, boolean override) {
        return isEligible(new CommonSlashCommandEvent(event), override);
    }

    public static boolean isEligible(CommonEvent event, boolean override) {
        Guild guild = event.getGuild();

        AudioTrack audioTrack = PlayerManager
                .getInstance()
                .getMusicManager(guild)
                .audioPlayer
                .getPlayingTrack();

        Member self = guild.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You must be in voice channel to use this command");
            return false;
        }

        if (!selfVoiceState.inAudioChannel()) {
            event.reply("I am not currently in a voice channel");
            return false;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.reply("You must be in the same channel as me to use this command");
            return false;
        }

        if (audioTrack == null && !override) {
            event.reply("Nothing is playing right now");
            return false;
        }

        return true;
    }
}
