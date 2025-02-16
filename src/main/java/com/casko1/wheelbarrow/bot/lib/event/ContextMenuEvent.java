package com.casko1.wheelbarrow.bot.lib.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class ContextMenuEvent implements CommonEvent {
    public final MessageContextInteractionEvent event;

    public ContextMenuEvent(MessageContextInteractionEvent event) {
        this.event = event;
    }


    public void reply(String message) {
        event.getHook().editOriginal(message).queue();
    }

    public void replyEmbed(EmbedBuilder eb) {
        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    public void replyEmbed(EmbedBuilder eb, File image) {
        FileUpload fileUpload = FileUpload.fromData(image, "thumbnail.png");
        event.getHook().sendMessageEmbeds(eb.build()).addFiles(fileUpload).queue();
    }

    public void editOriginalEmbeds(MessageEmbed eb) {
        event.getHook().editOriginalEmbeds(eb).queue();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public GuildVoiceState getSelfVoiceState() {
        return getGuild().getSelfMember().getVoiceState();
    }

    public TextChannel getTextChannel() {
        return null;
    }

    public Member getMember() {
        return event.getMember();
    }

    public User getAuthor() {
        return event.getUser();
    }

    public MessageContextInteractionEvent getEvent() {
        return event;
    }

    public void deferReply() {
        event.deferReply().queue();
    }

    public OptionMapping getOption(String name) {
        return event.getOption(name);
    }

    public Message getTarget() {
        return event.getTarget();
    }
}
