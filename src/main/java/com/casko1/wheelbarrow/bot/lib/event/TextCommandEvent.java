package com.casko1.wheelbarrow.bot.lib.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.function.Consumer;

public class TextCommandEvent implements CommonEvent {
    public final MessageReceivedEvent event;
    private final String[] args;

    public TextCommandEvent(MessageReceivedEvent event, String[] args) {
        this.event = event;
        this.args = args;
    }

    public void reply(String message) {
        MessageChannelUnion channel = event.getChannel();
        channel.sendMessage(message).queue();
    }

    @Override
    public void replyEmbed(EmbedBuilder eb) {
        getTextChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void replyEmbed(EmbedBuilder eb, File image) {
        FileUpload fileUpload = FileUpload.fromData(image, "thumbnail.png");
        getTextChannel().sendMessageEmbeds(eb.build()).addFiles(fileUpload).queue();
    }

    public void reply(String message, Consumer<Message> success) {
        MessageChannelUnion channel = event.getChannel();
        channel.sendMessage(message).queue(success);
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public Guild getGuild() {
        try {
            return event.getGuild();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    @Override
    public GuildVoiceState getSelfVoiceState() {
        Member selfMember = getSelfMember();
        return selfMember == null ? null : selfMember.getVoiceState();
    }

    public Member getSelfMember() {
        Guild guild = getGuild();
        return guild == null ? null : guild.getSelfMember();
    }

    public Member getMember() {
        return event.getMember();
    }

    @Override
    public User getAuthor() {
        return event.getAuthor();
    }

    public TextChannel getTextChannel() {
        return event.getChannel().asTextChannel();
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public String[] getArgs() {
        return args;
    }
}
