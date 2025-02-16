package com.casko1.wheelbarrow.bot.lib.command;

import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.lib.event.SlashCommandEvent;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public abstract class HybridCommand extends SlashCommand implements TextCommandInterface, SlashCommandInterface {

    @Override
    public void execute(SlashCommandEvent event) {
        event.deferReply();
        execute((CommonEvent) event);
    }

    public void execute(TextCommandEvent event) {
        execute((CommonEvent) event);
    }


    protected abstract void execute(CommonEvent event);

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {}
}
