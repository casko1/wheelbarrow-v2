package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class CommonSlashCommandEvent implements CommonEvent {

    public final SlashCommandEvent event;

    public CommonSlashCommandEvent(SlashCommandEvent event) {
        this.event = event;
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

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public GuildVoiceState getSelfVoiceState() {
        return getGuild().getSelfMember().getVoiceState();
    }

    @Override
    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    @Override
    public Member getMember() {
        return event.getMember();
    }
}
