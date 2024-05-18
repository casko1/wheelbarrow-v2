package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.commands.hybrid.SimpleHybridCommand;
import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.TrackScheduler;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;

public class ShuffleHybridCommand extends SimpleHybridCommand {

    public ShuffleHybridCommand() {
        this.name = "shuffle";
        this.help = "Shuffles current queue.";
        this.guildOnly = false;
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
