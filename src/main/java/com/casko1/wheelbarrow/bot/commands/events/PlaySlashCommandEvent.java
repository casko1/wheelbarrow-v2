package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class PlaySlashCommandEvent extends CommonSlashCommandEvent implements PlayEvent {

    private String args;
    private final boolean isUrl;

    public PlaySlashCommandEvent(SlashCommandEvent event) {
        super(event);
        this.args = initializeArgs(event);
        this.isUrl = ArgumentsUtil.isUrl(args);
    }

    private String initializeArgs(SlashCommandEvent event) {
        OptionMapping argsOption = event.getOption("url-or-search");
        return argsOption == null ? "" : argsOption.getAsString();
    }

    @Override
    public String getArgs() {
        return args;
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
        args = url;
    }

    @Override
    public boolean verifyCommandArguments() {
        if (args.length() == 0) {
            reply("URL/Search cannot be empty");
            return false;
        } else {
            return true;
        }
    }
}
