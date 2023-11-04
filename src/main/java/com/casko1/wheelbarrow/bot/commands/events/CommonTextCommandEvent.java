package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.CommonEvent;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class CommonTextCommandEvent implements CommonEvent {

    public final CommandEvent event;

    public CommonTextCommandEvent(CommandEvent event) {
        this.event = event;
    }

    @Override
    public void reply(String message) {
        event.reply(message);
    }

    @Override
    public void replyEmbed(EmbedBuilder eb) {
        event.getTextChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void replyEmbed(EmbedBuilder eb, File image) {
        FileUpload fileUpload = FileUpload.fromData(image, "thumbnail.png");
        event.getTextChannel().sendMessageEmbeds(eb.build()).addFiles(fileUpload).queue();
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public GuildVoiceState getSelfVoiceState() {
        return event.getSelfMember().getVoiceState();
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
