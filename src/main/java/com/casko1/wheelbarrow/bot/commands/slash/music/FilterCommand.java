package com.casko1.wheelbarrow.bot.commands.slash.music;

import com.casko1.wheelbarrow.bot.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.filters.FilterConfig;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FilterCommand extends SlashCommand {

    public FilterCommand(){
        this.name = "filter";
        this.options = Arrays.asList(
                new OptionData(OptionType.STRING, "type", "Type of filter", true),
                new OptionData(OptionType.STRING, "option", "Option of the filter", true, true),
                new OptionData(OptionType.STRING, "value",
                        "Value of the option (use anything for 'disable' option)", true)
        );

        String[] filters = new String[]{"bassboost", "distortion", "karaoke", "rotation", "timescale", "tremolo"};
        for(String filter : filters) this.options.get(0).addChoices(new Command.Choice(filter, filter));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();

        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        FilterConfiguration config = guildMusicManager.getFilterConfiguration();

        String type = event.getOption("type").getAsString();
        String option = event.getOption("option").getAsString();
        String value = event.getOption("value").getAsString();
        FilterConfig filter = parseFilter(type, config);

        if(option.equals("disable")) {
            disableFilter(event, filter, guildMusicManager, type);
        }
        else {
            applyFilter(event, filter, guildMusicManager, type, option, value);
        }
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        super.onAutoComplete(event);
        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        FilterConfiguration config = guildMusicManager.getFilterConfiguration();

        String type = event.getOption("type").getAsString();
        List<String> options = config.getConfigs().get(type).getOptions();
        List<Command.Choice> choices = new ArrayList<>();

        for(String op : options) choices.add(new Command.Choice(op, op));

        event.replyChoices(choices).queue();
    }

    private void disableFilter(SlashCommandEvent event, FilterConfig config,
                               GuildMusicManager guildMusicManager, String filterName){
        event.getHook().editOriginal(String.format("Disabling **%s** filter.", filterName)).queue();
        config.disable();
        guildMusicManager.setFilters();
    }

    private void applyFilter(SlashCommandEvent event, FilterConfig filterConfig, GuildMusicManager guildMusicManager,
                             String filterName, String option, String value){
        float factor = Float.parseFloat(value);

        boolean enabled = filterConfig.isEnabled();

        if(filterConfig.applyConfig(option, factor)){
            event.getHook().editOriginal(String.format("Setting %s/**%s** to **%.1fx**", filterName, option, factor)).queue();

            if(!enabled) guildMusicManager.setFilters();
        }
        else{
            event.getHook().editOriginal("Incorrect command usage").queue();
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
