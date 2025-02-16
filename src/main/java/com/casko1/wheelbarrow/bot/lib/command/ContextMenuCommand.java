package com.casko1.wheelbarrow.bot.lib.command;

import com.casko1.wheelbarrow.bot.lib.event.ContextMenuEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class ContextMenuCommand extends Command {
    public CommandData buildCommandData() {
        return Commands.message(getName());
    }

    public abstract void execute(ContextMenuEvent event);
}
