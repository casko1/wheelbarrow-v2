package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.commands.hybrid.SimpleHybridCommand;
import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;

public class LoopHybridCommand extends SimpleHybridCommand {

    public LoopHybridCommand() {
        this.name = "loop";
        this.help = "Loops current track";
        this.guildOnly = false;
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
