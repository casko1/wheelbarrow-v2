package com.casko1.wheelbarrow.bot.lib.command;

import com.casko1.wheelbarrow.bot.lib.event.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface SlashCommandInterface {
    void execute(SlashCommandEvent event);

    void onAutoComplete(CommandAutoCompleteInteractionEvent event);
}
