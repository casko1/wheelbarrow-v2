package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.entities.AdditionalTrackData;
import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.TimeConverterUtil;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class NowPlayingHybridCommand extends HybridCommand {

    public NowPlayingHybridCommand() {
        this.name = "nowplaying";
        this.description = "Displays information about current track.";
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            final AudioPlayer audioPlayer = musicManager.audioPlayer;

            final AudioTrack audioTrack = audioPlayer.getPlayingTrack();

            final AudioTrackInfo info = audioTrack.getInfo();

            final AdditionalTrackData addTrackData = audioTrack.getUserData(AdditionalTrackData.class);

            buildEmbed(info, audioTrack, addTrackData, event);
        }
    }

    private void buildEmbed(AudioTrackInfo info, AudioTrack audioTrack, AdditionalTrackData addTrackData, CommonEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLUE);

        eb.addField("Now playing", String.format("[%s by %s](%s)", info.title, info.author, info.uri), false);

        String currentTime = TimeConverterUtil.getMinutesAndSeconds(audioTrack.getPosition());
        eb.addField("Currently at:", String.format("%s of %s", currentTime, addTrackData.getDuration()), true);

        eb.addField("Requested by: ", addTrackData.getRequester().getAsMention(), true);

        if (addTrackData.getThumbnail().equals("attachment")) {
            //default case
            eb.setThumbnail("attachment://thumbnail.png");
            event.replyEmbed(eb, addTrackData.getDefaultImage());
        } else {
            //spotify api has found thumbnail
            eb.setThumbnail(addTrackData.getThumbnail());
            event.replyEmbed(eb);
        }
    }
}
