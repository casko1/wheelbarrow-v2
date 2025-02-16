package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayTextCommandEvent extends TextCommandEvent implements PlayEvent {
    private final boolean isUrl;
    private final boolean shuffle;
    private String query;


    public PlayTextCommandEvent(TextCommandEvent event) {
        super(event.getEvent(), event.getArgs());
        String[] inputArgs = event.getArgs();
        shuffle = setShuffle(inputArgs);

        if (shuffle) {
            query = Arrays.stream(inputArgs).limit(inputArgs.length - 1).collect(Collectors.joining("\\s+"));
        } else if (inputArgs.length > 0) {
            query = inputArgs[0];
        }

        isUrl = ArgumentsUtil.isUrl(query);
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public void setUrl(String url) {
        query = url;
    }

    private boolean setShuffle(String[] inputArgs) {
        int length = inputArgs.length;
        return inputArgs[length - 1].equals("-s");
    }

    @Override
    public boolean getShuffle() {
        return shuffle;
    }

    @Override
    public boolean isUrl() {
        return isUrl;
    }

    @Override
    public boolean verifyCommandArguments() {
        if (query.isBlank()) {
            this.reply("You need to provide link or a query");
            return false;
        }

        return true;
    }
}
