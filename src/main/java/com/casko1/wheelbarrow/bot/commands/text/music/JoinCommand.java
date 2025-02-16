package com.casko1.wheelbarrow.bot.commands.text.music;

import com.casko1.wheelbarrow.bot.lib.command.TextCommand;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@SuppressWarnings("ConstantConditions")
public class JoinCommand extends TextCommand {

    public JoinCommand() {
        this.name = "join";
        //this.help = "Makes bot join the voice channel you are currently in.";
    }

    @Override
    public void execute(TextCommandEvent event) {
        Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (selfVoiceState.inAudioChannel()) {
            event.reply("Already in voice channel");
            return;
        }

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You must be in voice channel to use this command");
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioChannel voiceChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(voiceChannel);

        TextChannel channel = event.getTextChannel();

        PlayerManager.getInstance().setTextChannel(event.getGuild(), channel);

        event.reply(String.format("Joining %s", voiceChannel.getName()));
    }
}
