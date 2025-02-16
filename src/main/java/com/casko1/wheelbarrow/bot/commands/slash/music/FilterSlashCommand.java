package com.casko1.wheelbarrow.bot.commands.slash.music;

import com.casko1.wheelbarrow.bot.lib.command.SlashCommand;
import com.casko1.wheelbarrow.bot.lib.event.SlashCommandEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.filters.FilterConfig;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.*;


public class FilterSlashCommand extends SlashCommand {

    private final String[] filters = new String[]{"bassboost", "distortion", "karaoke", "rotation", "timescale", "tremolo"};

    public FilterSlashCommand() {
        this.name = "filter";
        this.description = "Applies filter(s) to currently playing track.";
        this.subcommands = Arrays.asList(new Type(), new Disable());
    }

    @Override
    public void execute(SlashCommandEvent event) {
        executeCommand(event, event.getEvent().getSubcommandName());
    }

    public void executeCommand(SlashCommandEvent event, String subCommand) {
        //TODO check if user is in a voice channel
        event.deferReply();

        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        FilterConfiguration config = guildMusicManager.getFilterConfiguration();
        String type = event.getOption("type").getAsString();
        FilterConfig filter = parseFilter(type, config);

        if (subCommand.equals("disable")) {
            disableFilter(event, filter, guildMusicManager, type);
        } else {
            String option = event.getOption("option").getAsString();
            String value = event.getOption("value").getAsString();

            if (!ArgumentsUtil.isFloat(value)) {
                event.reply("Value must be a number");
                return;
            }

            applyFilter(event, filter, guildMusicManager, type, option, value);
        }
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getSubcommandName().equals("disable")) {
            this.subcommands.get(1).onAutoComplete(event);
            return;
        }

        this.subcommands.get(0).onAutoComplete(event);
    }

    private void disableFilter(SlashCommandEvent event, FilterConfig config,
                               GuildMusicManager guildMusicManager, String filterName) {
        event.reply(String.format("Disabling **%s** filter.", filterName));
        config.disable();
        guildMusicManager.setFilters();
    }

    private void applyFilter(SlashCommandEvent event, FilterConfig filterConfig, GuildMusicManager guildMusicManager,
                             String filterName, String option, String value) {
        float factor = Float.parseFloat(value);

        boolean enabled = filterConfig.isEnabled();

        if (filterConfig.applyConfig(option, factor)) {
            event.reply(String.format("Setting %s/**%s** to **%.1fx**", filterName, option, factor));

            if (!enabled) guildMusicManager.setFilters();
        } else {
            event.reply("Incorrect command usage");
        }
    }

    private FilterConfig parseFilter(String filterName, FilterConfiguration config) {
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
            this.description = "Applies filter(s) to currently playing track.";
            this.options = List.of(
                    new OptionData(OptionType.STRING, "type", "Type of filter", true),
                    new OptionData(OptionType.STRING, "option", "Option of the filter", true, true),
                    //fix that input may be string
                    new OptionData(OptionType.STRING, "value", "Value of the option", true)
            );

            for (String filter : filters) this.options.get(0).addChoice(filter, filter);
        }

        @Override
        public void execute(SlashCommandEvent event) {}

        @Override
        public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
            super.onAutoComplete(event);

            if (event.getOption("type") == null) {
                event.replyChoices(Collections.emptyList()).queue();
                return;
            }

            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            FilterConfiguration config = guildMusicManager.getFilterConfiguration();

            String type = event.getOption("type").getAsString();
            List<String> options = config.getConfigs().get(type).getOptions();
            List<Command.Choice> choices = new ArrayList<>();

            for (String op : options) choices.add(new Command.Choice(op, op));

            event.replyChoices(choices).queue();
        }
    }

    private class Disable extends SlashCommand {

        public Disable() {
            this.name = "disable";
            this.description = "Disable specific filter.";
            this.options = List.of(
                    new OptionData(
                            OptionType.STRING, "type", "Type of filter", true, true
                    )
            );
        }

        @Override
        public void execute(SlashCommandEvent event) {}

        @Override
        public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            HashMap<String, FilterConfig> configs = guildMusicManager.getFilterConfiguration().getConfigs();

            List<Command.Choice> choices = new ArrayList<>();
            for (FilterConfig conf : configs.values()) {
                if (conf.isEnabled()) choices.add(new Command.Choice(conf.getName(), conf.getName()));
            }

            event.replyChoices(choices).queue();
        }
    }
}
