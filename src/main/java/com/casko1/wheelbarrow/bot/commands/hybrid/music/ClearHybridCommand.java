package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;

public class ClearHybridCommand extends HybridCommand {

    public ClearHybridCommand() {
        this.name = "clear";
        this.description = "Clears the entire queue";
    }

    @Override
    public void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            manager.trackScheduler.queue.clear();

            event.reply("Cleared the entire queue");
        }
    }
}
