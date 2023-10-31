package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlaySlashCommandEvent extends CommonSlashCommandEvent implements PlayEvent {
    private final boolean isUrl;

    public PlaySlashCommandEvent(SlashCommandEvent event, boolean isUrl) {
        super(event);
        this.isUrl = isUrl;
    }

    @Override
    public AudioManager getAudioManager() {
        return getGuild().getAudioManager();
    }

    @Override
    public String getUrl() {
        return event.hasOption("url") ?
                event.getOption("url").getAsString() : event.getOption("query").getAsString();
    }

    @Override
    public void setUrl(String url) {
    }

    @Override
    public boolean getShuffle() {
        return event.hasOption("shuffle") && event.getOption("shuffle").getAsBoolean();
    }

    public boolean isUrl() {
        return this.isUrl;
    }

    @Override
    public boolean verifyCommandArguments() {
        if (isUrl && !ArgumentsUtil.isUrl(getUrl())) {
            reply("You must provide an URL when using this command");
            return false;
        }

        if (!isUrl && !ArgumentsUtil.isUrl(getUrl())) {
            reply("You must select an option from the list");
            return false;
        }

        return true;
    }
}
