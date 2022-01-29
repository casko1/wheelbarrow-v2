package com.casko1.wheelbarrow.bot.commands.message.music;

import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SkipCommand extends Command {

    public SkipCommand(){
        this.name = "skip";
        this.help = "Skips the current track";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event, true)){
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            event.reply("Skipped current track.");
            musicManager.trackScheduler.nextTrack();
        }

    }
}
