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

import java.util.*;


public class FilterCommand extends SlashCommand {

    private final String[] filters = new String[]{"bassboost", "distortion", "karaoke", "rotation", "timescale", "tremolo"};;

    public FilterCommand(){
        this.name = "filter";
        this.children = new SlashCommand[]{new Type(), new Disable()};
    }

    @Override
    protected void execute(SlashCommandEvent event) {
    }

    public void executeCommand(SlashCommandEvent event, String subCommand) {
        event.deferReply().queue();

        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        FilterConfiguration config = guildMusicManager.getFilterConfiguration();
        String type = event.getOption("type").getAsString();
        FilterConfig filter = parseFilter(type, config);

        if(subCommand.equals("disable")) {
            disableFilter(event, filter, guildMusicManager, type);
        }
        else {
            String option = event.getOption("option").getAsString();
            String value = event.getOption("value").getAsString();
            applyFilter(event, filter, guildMusicManager, type, option, value);
        }
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        super.onAutoComplete(event);

        if(event.getSubcommandName().equals("disable")) {
            this.children[1].onAutoComplete(event);
            return;
        }

        this.children[0].onAutoComplete(event);
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

    private class Type extends SlashCommand {

        public Type() {
            this.name = "type";
            this.options = Arrays.asList(
                    new OptionData(OptionType.STRING, "type", "Type of filter", true),
                    new OptionData(OptionType.STRING, "option", "Option of the filter", true, true),
                    //fix that input may be string
                    new OptionData(OptionType.STRING, "value", "Value of the option", true)
            );

            for(String filter : filters) this.options.get(0).addChoice(filter, filter);
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            FilterCommand.this.executeCommand(event, "type");
        }

        @Override
        public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
            super.onAutoComplete(event);

            if(event.getOption("type") == null) {
                event.replyChoices(Collections.emptyList()).queue();
                return;
            }

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            FilterConfiguration config = guildMusicManager.getFilterConfiguration();

            String type = event.getOption("type").getAsString();
            List<String> options = config.getConfigs().get(type).getOptions();
            List<Command.Choice> choices = new ArrayList<>();

            for(String op : options) choices.add(new Command.Choice(op, op));

            event.replyChoices(choices).queue();
        }
    }

    private class Disable extends SlashCommand {

        public Disable() {
            this.name = "disable";
            this.options = Collections.singletonList(
                    new OptionData(
                            OptionType.STRING, "type", "Type of filter", true, true
                    )
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            FilterCommand.this.executeCommand(event, "disable");
        }

        @Override
        public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
            super.onAutoComplete(event);

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            HashMap<String, FilterConfig> configs = guildMusicManager.getFilterConfiguration().getConfigs();

            List<Command.Choice> choices = new ArrayList<>();
            for(FilterConfig conf : configs.values()) {
                if(conf.isEnabled()) choices.add(new Command.Choice(conf.getName(), conf.getName()));
            }

            event.replyChoices(choices).queue();
        }
    }
}
