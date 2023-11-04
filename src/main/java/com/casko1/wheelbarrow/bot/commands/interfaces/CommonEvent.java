package com.casko1.wheelbarrow.bot.commands.interfaces;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;

public interface CommonEvent {
    void reply(String message);

    void replyEmbed(EmbedBuilder eb);

    void replyEmbed(EmbedBuilder eb, File image);

    Guild getGuild();

    GuildVoiceState getSelfVoiceState();

    TextChannel getTextChannel();

    Member getMember();
}
