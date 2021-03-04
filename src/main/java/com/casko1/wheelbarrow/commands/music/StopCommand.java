package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@SuppressWarnings("ConstantConditions")
public class StopCommand extends Command {

    public StopCommand(){
        this.name = "stop";
        this.help = "Makes the bot stop playing music.";
        this.guildOnly = false;
        this.aliases = new String[]{"leave"};
    }

    @Override
    protected void execute(CommandEvent event) {
        Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inVoiceChannel()){
            event.reply("You must be in voice channel to use this command.");
            return;
        }

        if(!selfVoiceState.inVoiceChannel()){
            event.reply("I am not currently in a voice channel!");
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("You must be in the same channel as me to use this command!");
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.trackScheduler.player.stopTrack();
        musicManager.trackScheduler.queue.clear();

        PlayerManager.getInstance().removeTextChannelManager(event.getGuild());

        final AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.closeAudioConnection();

        event.reply("Leaving the voice channel.");
    }
}
