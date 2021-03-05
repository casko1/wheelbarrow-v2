package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@SuppressWarnings("ConstantConditions")
public class JoinCommand extends Command {

    public JoinCommand(){
        this.name = "join";
        this.help = "Makes bot join the voice channel you are currently in.";
        this.guildOnly = false;
    }


    @Override
    protected void execute(CommandEvent event) {
        Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(selfVoiceState.inVoiceChannel()){
            event.reply("Already in voice channel.");
            return;
        }

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inVoiceChannel()){
            event.reply("You must be in voice channel to use this command.");
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        VoiceChannel voiceChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(voiceChannel);

        TextChannel channel = event.getTextChannel();

        PlayerManager.getInstance().setTextChannel(event.getGuild(), channel);

        event.replyFormatted("Joining %s.", voiceChannel.getName());
    }
}
