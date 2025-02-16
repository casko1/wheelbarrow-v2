package com.casko1.wheelbarrow.bot.lib.command;

public abstract class Command {
    protected String name;
    protected String description;
    protected String usage;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
