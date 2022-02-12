package com.casko1.wheelbarrow.bot.commands.slash.music;

import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.TrackScheduler;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ShuffleCommand extends Command {

    public ShuffleCommand() {
        this.name="shuffle";
        this.help = "Shuffles current queue.";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(VoiceStateCheckUtil.isEligible(event, false)){
            TrackScheduler trackScheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).trackScheduler;

            trackScheduler.shuffle();

            event.reply("Shuffled current queue.");
        }
    }
}
