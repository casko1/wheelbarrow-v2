package com.casko1.wheelbarrow.bot.commands.events;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayTextCommandEvent implements PlayEvent {

    public final CommandEvent event;
    private final boolean isUrl;
    private final boolean shuffle;
    private String args;


    public PlayTextCommandEvent(CommandEvent event){
        this.event = event;
        shuffle = setShuffle(event);

        if(shuffle) {
            String[] split = event.getArgs().split("\\s+");
            args = Arrays.stream(split).limit(split.length - 1).collect(Collectors.joining("\\s+"));
        }
        else {
            args = event.getArgs();
        }

        isUrl = ArgumentsUtil.isUrl(args);
    }

    @Override
    public AudioManager getAudioManager() {
        return event.getGuild().getAudioManager();
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

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public String getUrl() {
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
    public void reply(String message) {
        event.reply(message);
    }

    @Override
    public void replyEmbed(EmbedBuilder eb, File image) {
        if(image != null) {
            FileUpload fileUpload = FileUpload.fromData(image, "thumbnail.png");
            event.getTextChannel().sendMessageEmbeds(eb.build()).addFiles(fileUpload).queue();
        }
        else {
            event.getTextChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    @Override
    public boolean isUrl() {
        return isUrl;
    }

    @Override
    public boolean verifyCommandArguments() {
        if(event.getArgs().isBlank()){
            event.reply("You need to provide link or a query.");
            return false;
        }

        return true;
    }
}
