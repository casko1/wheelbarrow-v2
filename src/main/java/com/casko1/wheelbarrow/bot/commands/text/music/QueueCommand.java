package com.casko1.wheelbarrow.bot.commands.text.music;

import com.casko1.wheelbarrow.bot.lib.command.TextCommand;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import com.casko1.wheelbarrow.bot.music.QueuePaginator;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QueueCommand extends TextCommand {

    private final QueuePaginator.Builder builder;

    public QueueCommand(QueuePaginator.Builder builder) {
        this.name = "queue";
        this.builder = builder;
    }


    @Override
    public void execute(TextCommandEvent event) {

        if (VoiceStateCheckUtil.isEligible(event, false)) {

            GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            BlockingQueue<AudioTrack> queue = manager.trackScheduler.queue;

            renderQueue(queue, manager, event);
        }
    }

    public void renderQueue(BlockingQueue<AudioTrack> queue, GuildMusicManager manager, TextCommandEvent event) {

        List<String> list = new ArrayList<>();

        for (AudioTrack track : queue) {
            list.add(track.getInfo().title);
        }

        QueuePaginator paginator = builder
                .setItems(list)
                .setUsers(event.getAuthor())
                .setCurrentTrack(manager.audioPlayer.getPlayingTrack().getInfo().title)
                .setItemsPerPage(10)
                .setColor(Color.BLUE)
                .build();

        paginator.paginate(event.getTextChannel(), 1);

    }

}
