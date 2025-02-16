package com.casko1.wheelbarrow.bot.commands.text.music;

import com.casko1.wheelbarrow.bot.lib.command.TextCommand;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.TrackScheduler;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RemoveCommand extends TextCommand {

    public RemoveCommand() {
        this.name = "remove";
        this.description = "Removes a track from the queue";
        this.usage = "<position in queue>";
    }


    @Override
    public void execute(TextCommandEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            String[] args = event.getArgs();

            if (ArgumentsUtil.isInteger(args[0])) {
                TrackScheduler trackScheduler = guildMusicManager.trackScheduler;

                int position = Integer.parseInt(args[0]);

                if (position < 1 || position > trackScheduler.queue.size()) {
                    event.reply("Position out of bounds");
                } else {
                    trackScheduler.remove(position);
                    event.reply(String.format("Removed track at position %d", position));
                }
            } else {
                event.reply("Incorrect command usage");
            }
        }

    }
}
