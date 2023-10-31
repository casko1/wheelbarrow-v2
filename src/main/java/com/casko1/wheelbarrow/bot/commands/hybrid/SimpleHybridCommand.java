package com.casko1.wheelbarrow.bot.commands.hybrid;

import com.casko1.wheelbarrow.bot.commands.events.CommonSlashCommandEvent;
import com.casko1.wheelbarrow.bot.commands.events.CommonTextCommandEvent;
import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public abstract class SimpleHybridCommand extends SlashCommand {

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();
        execute(new CommonSlashCommandEvent(event));
    }

    @Override
    protected void execute(CommandEvent event) {
        execute(new CommonTextCommandEvent(event));
    }

    protected abstract void execute(CommonEvent event);
}
