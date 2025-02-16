package com.casko1.wheelbarrow.bot.lib.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.ArrayList;
import java.util.List;

public abstract class SlashCommand extends Command implements SlashCommandInterface {
    protected List<OptionData> options;
    protected List<SlashCommand> subcommands;

    public CommandData buildCommandData() {
        SlashCommandData data = Commands.slash(getName(), getDescription());

        if (options != null && !options.isEmpty()) {
            data.addOptions(options);
        }

        List<SubcommandData> subcommandsData = new ArrayList<>();

        if (subcommands != null && !subcommands.isEmpty()) {
            for (SlashCommand subcommand: subcommands) {
                SubcommandData subcommandData = new SubcommandData(subcommand.getName(), subcommand.getDescription());

                if (subcommand.options != null && !subcommand.options.isEmpty()) {
                    subcommandData.addOptions(subcommand.options);
                }

                subcommandsData.add(subcommandData);
            }
        }

        if (!subcommandsData.isEmpty()) {
            data.addSubcommands(subcommandsData);
        }

        return data;
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {}
}
