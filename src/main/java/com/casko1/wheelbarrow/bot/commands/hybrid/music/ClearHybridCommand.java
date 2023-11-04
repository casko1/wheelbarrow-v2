package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.commands.hybrid.SimpleHybridCommand;
import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;

public class ClearHybridCommand extends SimpleHybridCommand {

    public ClearHybridCommand() {
        this.name = "clear";
        this.help = "Clears the entire queue";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            manager.trackScheduler.queue.clear();

            event.reply("Cleared the entire queue.");
        }
    }
}
