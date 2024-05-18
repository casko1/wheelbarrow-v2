package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class PlaySlashCommandEvent extends CommonSlashCommandEvent implements PlayEvent {
    private final boolean isQuery;

    public PlaySlashCommandEvent(SlashCommandEvent event, boolean isQuery) {
        super(event);
        this.isQuery = isQuery;
    }

    @Override
    public String getArgs() {
        return event.hasOption("url") ? event.getOption("url").getAsString() : event.getOption("query").getAsString();
    }

    @Override
    public boolean getShuffle() {
        return event.hasOption("shuffle") && event.getOption("shuffle").getAsBoolean();
    }

    @Override
    public boolean isUrl() {
        return true;
    }

    @Override
    public void setUrl(String url) {
    }

    @Override
    public boolean verifyCommandArguments() {
        boolean isUrl = ArgumentsUtil.isUrl(getArgs());

        if (!isUrl) {
            if (!isQuery) {
                reply("You must provide an URL when using this command");
            } else {
                reply("You must select an option from the list");
            }

            return false;
        } else {
            return true;
        }
    }
}
