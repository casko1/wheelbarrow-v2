package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;


public class SpeedCommand extends Command {

    public SpeedCommand(){
        this.name = "speed";
        this.help = "Sets the speed of the track.";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){

            if(ArgumentsUtil.isDouble(event.getArgs())){

                double speed = Double.parseDouble(event.getArgs());

                GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

                FilterConfiguration config = guildMusicManager.getFilterConfiguration();

                if(!config.timescale.isEnabled()){
                    config.timescale.enable();
                    config.timescale.setSpeed(speed);
                    guildMusicManager.setFilters();
                }
                else{
                    config.timescale.setSpeed(speed);
                    config.timescale.updateFilter();
                }

                event.reply(String.format("Setting speed to **%.1fx**", speed));

            }
            else{
                event.reply("You must provide a number.");
            }

        }

    }
}
