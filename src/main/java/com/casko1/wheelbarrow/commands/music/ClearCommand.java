package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ClearCommand extends Command {

    public ClearCommand(){
        this.name = "clear";
        this.help = "Clears the entire queue";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(VoiceStateCheckUtil.isEligible(event, false)){
            GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            manager.trackScheduler.queue.clear();

            event.reply("Cleared the entire queue.");
        }
    }
}
