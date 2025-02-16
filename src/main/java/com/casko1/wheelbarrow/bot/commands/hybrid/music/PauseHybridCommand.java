package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class PauseHybridCommand extends HybridCommand {

    public PauseHybridCommand() {
        this.name = "pause";
        this.description = "Pauses the current track";
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, true)) {
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            AudioPlayer player = musicManager.audioPlayer;
            boolean isPaused = player.isPaused();

            player.setPaused(!isPaused);

            if (isPaused) {
                event.reply(":arrow_forward: Un-pausing playback");
            } else {
                event.reply(":pause_button: Pausing playback");
            }
        }
    }
}
