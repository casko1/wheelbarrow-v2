package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;


public class TimescaleCommand extends Command {

    public TimescaleCommand(){
        this.name = "timescale";
        this.help = "**Applies timescale filter to current track.** *Example: $$timescale speed 1.2*";
        this.arguments = "<speed | pitch | rate> <number>";
        this.aliases = new String[]{"ts"};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){

            String[] args = event.getArgs().split(" ");

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            FilterConfiguration config = guildMusicManager.getFilterConfiguration();

            if(args.length == 1 && args[0].equals("disable")){
                event.reply("Disabling **Timescale** filter.");
                config.timescale.disableFilter();
                guildMusicManager.setFilters();
                return;
            }

            if(args.length > 1 && ArgumentsUtil.isDouble(args[1])){

                double factor = Double.parseDouble(args[1]);

                if(!config.timescale.isEnabled()){
                    config.timescale.enable();

                    if(!applyFactor(args[0], factor, config)){
                        event.reply("Incorrect command usage");
                        config.timescale.disableFilter();
                        return;
                    }

                    guildMusicManager.setFilters();
                }
                else{

                    if(!applyFactor(args[0], factor, config)){
                        event.reply("Incorrect command usage");
                        return;
                    }

                    config.timescale.updateFilter();
                }

                event.reply(String.format("Setting timescale/**%s** to **%.1fx**", args[0], factor));

            }
            else{
                event.reply("Incorrect command usage");
            }

        }

    }

    //returns true if applying factor is successful, false otherwise
    private boolean applyFactor(String setting, double factor, FilterConfiguration config){

        switch (setting) {
            case "speed" -> config.timescale.setSpeed(factor);
            case "pitch" -> config.timescale.setPitch(factor);
            case "rate" -> config.timescale.setRate(factor);
            default -> {
                return false;
            }
        }

        return true;
    }
}
