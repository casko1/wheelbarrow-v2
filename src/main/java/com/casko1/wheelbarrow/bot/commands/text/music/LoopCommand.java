package com.casko1.wheelbarrow.bot.commands.text.music;

import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class LoopCommand extends Command {

    public LoopCommand() {
        this.name = "loop";
        this.help = "Loops current track";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
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
