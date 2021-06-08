package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.music.lavaplayer.TrackScheduler;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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
