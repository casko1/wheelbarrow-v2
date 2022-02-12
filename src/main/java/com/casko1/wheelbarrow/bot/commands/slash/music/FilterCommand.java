package com.casko1.wheelbarrow.bot.commands.slash.music;

import com.casko1.wheelbarrow.bot.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.filters.FilterConfig;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;


public class FilterCommand extends Command {

    private final String filterName;

    public FilterCommand(String filterName){
        this.filterName = filterName;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(VoiceStateCheckUtil.isEligible(event, false)){

            String[] args = event.getArgs().split(" ");

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            FilterConfiguration config = guildMusicManager.getFilterConfiguration();

            if(args.length == 1 && args[0].equals("disable")){
                disableFilter(event, parseFilter(filterName, config), guildMusicManager);
                return;
            }

            if(args.length > 1 && ArgumentsUtil.isFloat(args[1])){
                applyFilter(event, parseFilter(filterName, config), guildMusicManager, args);
            }
            else{
                event.reply("Incorrect command usage");
            }
        }
    }

    private void disableFilter(CommandEvent event, FilterConfig config, GuildMusicManager guildMusicManager){
        event.reply(String.format("Disabling **%s** filter.", filterName));
        config.disable();
        guildMusicManager.setFilters();
    }

    private void applyFilter(CommandEvent event, FilterConfig filterConfig, GuildMusicManager guildMusicManager, String[] args){
        float factor = Float.parseFloat(args[1]);

        boolean enabled = filterConfig.isEnabled();

        if(filterConfig.applyConfig(args, factor)){
            event.reply(String.format("Setting %s/**%s** to **%.1fx**", filterName, args[0], factor));

            if(!enabled) guildMusicManager.setFilters();
        }
        else{
            event.reply("Incorrect command usage");
        }
    }

    private FilterConfig parseFilter(String filterName, FilterConfiguration config){
        FilterConfig filterConfig = null;

        switch (filterName) {
            case "timescale" -> filterConfig = config.timescale;
            case "karaoke" -> filterConfig = config.karaoke;
            case "distortion" -> filterConfig = config.distortion;
            case "tremolo" -> filterConfig = config.tremolo;
            case "rotation" -> filterConfig = config.rotation;
            case "bassboost" -> filterConfig = config.bassboost;
        }

        return filterConfig;
    }
}
