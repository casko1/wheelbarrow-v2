package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class DistortionCommand extends Command {

    public DistortionCommand(){
        this.name = "distortion";
        this.help = "Distorts the current track.";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){

            if(ArgumentsUtil.isFloat(event.getArgs())){

                float scale = Float.parseFloat(event.getArgs());

                GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

                FilterConfiguration config = guildMusicManager.getFilterConfiguration();

                if(!config.distortion.isEnabled()){
                    config.distortion.enable();
                    config.distortion.setScale(scale);
                    guildMusicManager.setFilters();
                }
                else{
                    config.distortion.setScale(scale);
                    config.distortion.updateFilter();
                }

                event.reply(String.format("Setting distortion to **%.1fx**", scale));

            }
            else{
                event.reply("You must provide a number.");
            }

        }
    }
}
