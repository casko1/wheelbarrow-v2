package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.commands.hybrid.SimpleHybridCommand;
import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;

public class SkipHybridCommand extends SimpleHybridCommand {

    public SkipHybridCommand() {
        this.name = "skip";
        this.help = "Skips the current track";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, true)) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            event.reply("Skipped current track.");
            musicManager.trackScheduler.nextTrack();
        }
    }
}
