package com.casko1.wheelbarrow.bot.commands.text.music;

import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class StopCommand extends Command {

    public StopCommand() {
        this.name = "stop";
        this.help = "Makes the bot stop playing music.";
        this.guildOnly = false;
        this.aliases = new String[]{"leave", "disconnect", "exit"};
    }

    @Override
    protected void execute(CommandEvent event) {

        if (VoiceStateCheckUtil.isEligible(event, true)) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            musicManager.trackScheduler.player.stopTrack();
            musicManager.trackScheduler.queue.clear();

            PlayerManager.getInstance().removeTextChannel(event.getGuild());

            final AudioManager audioManager = event.getGuild().getAudioManager();

            audioManager.closeAudioConnection();

            PlayerManager.getInstance().removeMusicManager(event.getGuild().getIdLong());

            event.reply("Leaving the voice channel.");
        }

    }
}
