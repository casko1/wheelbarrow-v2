package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.music.lavaplayer.TrackScheduler;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RemoveCommand extends Command {

    public RemoveCommand(){
        this.name = "remove";
        this.help = "Removes a track from the queue";
        this.arguments  = "<position in queue>";
        this.guildOnly = false;
    }


    @Override
    protected void execute(CommandEvent event) {
        if(VoiceStateCheckUtil.isEligible(event, false)){
            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            String[] args = event.getArgs().split(" ");

            if(ArgumentsUtil.isInteger(args[0])){
                TrackScheduler trackScheduler = guildMusicManager.trackScheduler;

                int position = Integer.parseInt(args[0]);

                if(position < 1 || position > trackScheduler.queue.size()){
                    event.reply("Position out of bounds.");
                }
                else{
                    trackScheduler.remove(position);
                    event.reply(String.format("Removed track at position %d", position));
                }
            }
            else{
                event.reply("Incorrect command usage.");
            }
        }

    }
}
