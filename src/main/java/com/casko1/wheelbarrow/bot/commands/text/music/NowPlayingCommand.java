package com.casko1.wheelbarrow.bot.commands.text.music;

import com.casko1.wheelbarrow.bot.entities.AdditionalTrackData;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.TimeConverterUtil;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;

public class NowPlayingCommand extends Command {

    public NowPlayingCommand() {
        this.name = "nowplaying";
        this.help = "Displays information about current track.";
        this.guildOnly = false;
        this.aliases = new String[]{"np"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            final AudioPlayer audioPlayer = musicManager.audioPlayer;

            final AudioTrack audioTrack = audioPlayer.getPlayingTrack();

            final AudioTrackInfo info = audioTrack.getInfo();

            final AdditionalTrackData addTrackData = audioTrack.getUserData(AdditionalTrackData.class);

            buildEmbed(info, audioTrack, addTrackData, event);
        }
    }

    private void buildEmbed(AudioTrackInfo info, AudioTrack audioTrack, AdditionalTrackData addTrackData, CommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLUE);

        eb.addField("Now playing", String.format("[%s by %s](%s)", info.title, info.author, info.uri), false);

        String currentTime = TimeConverterUtil.getMinutesAndSeconds(audioTrack.getPosition());
        eb.addField("Currently at:", String.format("%s of %s", currentTime, addTrackData.getDuration()), true);

        eb.addField("Requested by: ", addTrackData.getRequester().getAsMention(), true);

        if (addTrackData.getThumbnail().equals("attachment")) {
            //default case
            FileUpload thumbnail = FileUpload.fromData(addTrackData.getDefaultImage());
            eb.setThumbnail("attachment://thumbnail.png");
            event.getTextChannel().sendMessageEmbeds(eb.build()).addFiles(thumbnail).queue();
        } else {
            //spotify api has found thumbnail
            eb.setThumbnail(addTrackData.getThumbnail());
            event.getTextChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }
}
