package com.casko1.wheelbarrow.bot.commands.text.music;

import com.casko1.wheelbarrow.bot.lib.command.TextCommand;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.TrackScheduler;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.bot.utils.TimeConverterUtil;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SeekCommand extends TextCommand {

    public SeekCommand() {
        this.name = "seek";
        this.description = "Seeks current track to specified timestamp (in seconds)";
        this.usage = "<timestamp in seconds>";
    }


    @Override
    public void execute(TextCommandEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            TrackScheduler trackScheduler = guildMusicManager.trackScheduler;

            String[] args = event.getArgs();

            if (ArgumentsUtil.isInteger(args[0]) && trackScheduler.seek((long) Integer.parseInt(args[0]) * 1000)) {
                sendTimestampMessage(event, Integer.parseInt(args[0]), trackScheduler.player.getPlayingTrack().getDuration());
            } else {
                event.reply("Track cannot be seeked or the command was used incorrectly");
            }
        }
    }


    private void sendTimestampMessage(TextCommandEvent event, int timeStamp, long trackDuration) {

        if (timeStamp <= 0) {
            event.reply("Seeking current track to 0:00");
        } else if (timeStamp * 1000 > trackDuration) {
            event.reply("Seeking beyond song duration. Skipping current track");
        } else {
            event.reply(String.format("Seeking current track to %s",
                    TimeConverterUtil.getMinutesAndSeconds(timeStamp * 1000)));
        }
    }
}
