package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.lib.event.SlashCommandEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class PlaySlashCommandEvent extends SlashCommandEvent implements PlayEvent {
    private String query;
    private final boolean isUrl;

    public PlaySlashCommandEvent(SlashCommandEvent event) {
        super(event.getEvent());
        query = initializeArgs(event);
        isUrl = ArgumentsUtil.isUrl(query);
    }

    private String initializeArgs(SlashCommandEvent event) {
        OptionMapping argsOption = event.getEvent().getOption("url-or-search");
        return argsOption == null ? "" : argsOption.getAsString();
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public boolean getShuffle() {
        OptionMapping shuffleOption = event.getOption("shuffle");
        return shuffleOption != null && shuffleOption.getAsBoolean();
    }

    @Override
    public boolean isUrl() {
        return isUrl;
    }

    @Override
    public void setUrl(String url) {
        query = url;
    }

    @Override
    public boolean verifyCommandArguments() {
        if (query.length() == 0) {
            reply("URL/Search cannot be empty");
            return false;
        } else {
            return true;
        }
    }
}
