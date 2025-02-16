package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.TrackScheduler;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;

public class ShuffleHybridCommand extends HybridCommand {

    public ShuffleHybridCommand() {
        this.name = "shuffle";
        this.description = "Shuffles current queue.";
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            TrackScheduler trackScheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).trackScheduler;

            trackScheduler.shuffle();

            event.reply("Shuffled current queue");
        }
    }
}
