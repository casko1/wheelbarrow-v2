package com.casko1.wheelbarrow.commands.music.lavaplayer;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

@SuppressWarnings("ConstantConditions")
public class SkipCommand extends Command {

    public SkipCommand(){
        this.name = "skip";
        this.help = "Skips the current track";
        this.guildOnly = false;
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

        if(audioPlayer.getPlayingTrack() == null){
            event.reply("Nothing is playing right now.");
            return;
        }

        musicManager.trackScheduler.nextTrack();
        event.reply("Skipped current track.");

    }
}
