package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayTextCommandEvent extends CommonTextCommandEvent implements PlayEvent {
    private final boolean isUrl;
    private final boolean shuffle;
    private String args;


    public PlayTextCommandEvent(CommandEvent event) {
        super(event);
        shuffle = setShuffle(event);

        if (shuffle) {
            String[] split = event.getArgs().split("\\s+");
            args = Arrays.stream(split).limit(split.length - 1).collect(Collectors.joining("\\s+"));
        } else {
            args = event.getArgs();
        }

        isUrl = ArgumentsUtil.isUrl(args);
    }

    @Override
    public String getArgs() {
        return args;
    }

    @Override
    public void setUrl(String url) {
        args = url;
    }

    private boolean setShuffle(CommandEvent event) {
        event.getArgs();
        List<String> split = new ArrayList<>(Arrays.asList(event.getArgs().split("\\s+")));

        return split.get(split.size() - 1).equals("-s");
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
        if (event.getArgs().isBlank()) {
            event.reply("You need to provide link or a query");
            return false;
        }

        return true;
    }
}
