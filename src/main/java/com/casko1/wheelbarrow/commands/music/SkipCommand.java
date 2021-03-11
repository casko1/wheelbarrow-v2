package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class SkipCommand extends Command {

    public SkipCommand(){
        this.name = "skip";
        this.help = "Skips the current track";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            final AudioPlayer audioPlayer = musicManager.audioPlayer;

            if(audioPlayer.getPlayingTrack() == null){
                event.reply("Nothing is playing right now.");
                return;
            }


            event.reply("Skipped current track.");
            musicManager.trackScheduler.nextTrack();
        }

    }
}
