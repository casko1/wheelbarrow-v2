package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class KaraokeCommand extends Command {

    public KaraokeCommand(){
        this.name = "karaoke";
        this.help = "**Applies karaoke filter to current track.** *Example: $$karaoke level 1.2*";
        this.arguments = "<mono|level> <number>";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){

            String[] args = event.getArgs().split(" ");

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            FilterConfiguration config = guildMusicManager.getFilterConfiguration();

            if(args.length == 1 && args[0].equals("disable")){
                event.reply("Disabling Karaoke filter.");
                config.karaoke.disable();
                guildMusicManager.setFilters();
                return;
            }

            if(args.length > 1 && ArgumentsUtil.isFloat(args[1])){

                float factor = Float.parseFloat(args[1]);

                if(!config.karaoke.isEnabled()){
                    config.karaoke.enable();

                    if(!applyFactor(args[0], factor, config)){
                        event.reply("Incorrect command usage");
                        config.karaoke.disable();
                        return;
                    }

                    guildMusicManager.setFilters();
                }
                else{

                    if(!applyFactor(args[0], factor, config)){
                        event.reply("Incorrect command usage");
                        return;
                    }

                    config.karaoke.updateFilter();
                }

                event.reply(String.format("Setting karaoke/**%s** to **%.1fx**", args[0], factor));

            }
            else{
                event.reply("Incorrect command usage");
            }

        }

    }

    private boolean applyFactor(String setting, float factor, FilterConfiguration config){

        switch (setting) {
            case "mono" -> config.karaoke.setMonoLevel(factor);
            case "level" -> config.karaoke.setLevel(factor);
            default -> {
                return false;
            }
        }

        return true;
    }
}
