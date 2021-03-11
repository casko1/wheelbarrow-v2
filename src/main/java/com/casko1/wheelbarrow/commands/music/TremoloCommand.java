package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class TremoloCommand extends Command {

    public TremoloCommand(){
        this.name = "tremolo";
        this.help = "**Applies tremolo filter to current track.** *Example: $$tremolo depth 1.2*";
        this.arguments = "<freq | depth> <number>";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){

            String[] args = event.getArgs().split(" ");

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            FilterConfiguration config = guildMusicManager.getFilterConfiguration();

            if(args.length == 1 && args[0].equals("disable")){
                event.reply("Disabling **Tremolo** filter.");
                config.tremolo.disable();
                guildMusicManager.setFilters();
                return;
            }

            if(args.length > 1 && ArgumentsUtil.isFloat(args[1])){

                float factor = Float.parseFloat(args[1]);

                if(!config.tremolo.isEnabled()){
                    config.tremolo.enable();

                    if(!applyFactor(args[0], factor, config)){
                        event.reply("Incorrect command usage");
                        config.tremolo.disable();
                        return;
                    }

                    guildMusicManager.setFilters();
                }
                else{

                    if(!applyFactor(args[0], factor, config)){
                        event.reply("Incorrect command usage");
                        return;
                    }

                    config.tremolo.updateFilter();
                }

                event.reply(String.format("Setting tremolo/**%s** to **%.1fx**", args[0], factor));

            }
            else{
                event.reply("Incorrect command usage");
            }
        }
    }

    //returns true if applying factor is successful, false otherwise
    private boolean applyFactor(String setting, float factor, FilterConfiguration config){
        switch (setting) {
            case "depth" -> config.tremolo.setDepth(factor);
            case "freq" -> config.tremolo.setFrequency(factor);
            default -> {
                return false;
            }
        }

        return true;
    }
}
