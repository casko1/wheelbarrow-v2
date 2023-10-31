package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class PlaySlashCommandEvent implements PlayEvent {

    public final SlashCommandEvent event;
    private final boolean isUrl;

    public PlaySlashCommandEvent(SlashCommandEvent event, boolean isUrl) {
        this.event = event;
        this.isUrl = isUrl;
    }

    @Override
    public AudioManager getAudioManager() {
        return event.getGuild().getAudioManager();
    }

    @Override
    public GuildVoiceState getSelfVoiceState() {
        return event.getGuild().getSelfMember().getVoiceState();
    }

    @Override
    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    @Override
    public Member getMember() {
        return event.getMember();
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
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

    @Override
    public void reply(String message) {
        event.getHook().editOriginal(message).queue();
    }

    @Override
    public void replyEmbed(EmbedBuilder eb, File image) {
        if (image != null) {
            FileUpload fileUpload = FileUpload.fromData(image, "thumbnail.png");
            event.getHook().sendMessageEmbeds(eb.build()).addFiles(fileUpload).queue();
        } else {
            event.getHook().sendMessageEmbeds(eb.build()).queue();
        }
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
