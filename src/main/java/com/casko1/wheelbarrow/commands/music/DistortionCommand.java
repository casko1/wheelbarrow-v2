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
        this.help = "**Distorts the current track.** *Example: $$distortion 1.2*";
        this.arguments = "<number>";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){

            String[] args = event.getArgs().split(" ");

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            FilterConfiguration config = guildMusicManager.getFilterConfiguration();

            if(args.length == 1){
                if(args[0].equals("disable")){
                    event.reply("Disabling **Distortion** filter.");
                    config.distortion.disable();
                    guildMusicManager.setFilters();
                    return;
                }

                if(ArgumentsUtil.isFloat(args[0])){
                    float factor = Float.parseFloat(args[0]);

                    if(!config.distortion.isEnabled()){
                        config.distortion.enable();
                        config.distortion.setScale(factor);
                        guildMusicManager.setFilters();
                    }
                    else{
                        config.distortion.setScale(factor);
                        config.distortion.updateFilter();
                    }

                    event.reply(String.format("Setting distortion to **%.1fx**", factor));
                }
                else{
                    event.reply("Incorrect command usage");
                }
            }
        }
    }
}
