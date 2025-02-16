package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;

public class LoopHybridCommand extends HybridCommand {

    public LoopHybridCommand() {
        this.name = "loop";
        this.description = "Loops current track";
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            boolean loopState = guildMusicManager.trackScheduler.isLoop();

            guildMusicManager.trackScheduler.toggleLoop();

            if (loopState) {
                event.reply(":repeat: Un-Looping current track");
            } else {
                event.reply(":repeat: Looping current track");
            }
        }
    }
}
