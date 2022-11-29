package com.casko1.wheelbarrow.bot.commands.interfaces;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;

public interface PlayEvent {

    AudioManager getAudioManager();

    GuildVoiceState getSelfVoiceState();

    TextChannel getTextChannel();

    Member getMember();

    Guild getGuild();

    String getUrl();

    void setUrl(String url);

    boolean getShuffle();

    void reply(String message);

    void replyEmbed(EmbedBuilder eb, File image);

    boolean isUrl();

    boolean verifyCommandArguments();
}
