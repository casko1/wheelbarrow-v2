package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

public class StopHybridCommand extends HybridCommand {

    public StopHybridCommand() {
        this.name = "stop";
        this.description = "Makes the bot stop playing music.";
        //this.aliases = new String[]{"leave", "disconnect", "exit"};
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, true)) {
            Guild guild = event.getGuild();

            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

            musicManager.trackScheduler.player.stopTrack();
            musicManager.trackScheduler.queue.clear();

            PlayerManager.getInstance().removeTextChannel(guild);

            final AudioManager audioManager = guild.getAudioManager();

            audioManager.closeAudioConnection();

            PlayerManager.getInstance().removeMusicManager(guild.getIdLong());

            event.reply("Leaving the voice channel");
        }
    }
}
