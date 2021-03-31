package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.music.QueuePaginator;
import com.casko1.wheelbarrow.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QueueCommand extends Command {

    private final QueuePaginator.Builder builder;

    public QueueCommand(QueuePaginator.Builder builder){
        this.name = "queue";
        this.help = "Return the current music queue.";
        this.aliases = new String[]{"q"};
        this.guildOnly = false;
        this.builder = builder;
    }


    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event, false)){

            GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            BlockingQueue<AudioTrack> queue = manager.trackScheduler.queue;

            //10 items per page
            if(queue.size() <= 10){
                singlePage(queue, manager, event);
            }
            else{
                multiplePages(queue, manager, event);
            }
        }
    }

    public void singlePage(BlockingQueue<AudioTrack> queue, GuildMusicManager manager, CommandEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLUE);

        List<AudioTrack> list = new ArrayList<>(queue);

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < list.size(); i++){
            sb.append(String.format("%d. %s\n", i+1, list.get(i).getInfo().title));
        }

        String text = sb.length() == 0 ? "Nothing is in the queue... yet" : sb.toString();

        eb.addField("Currently playing:", manager.audioPlayer.getPlayingTrack().getInfo().title, false);
        eb.addField("**Tracks in queue:**", text, false);
        eb.setFooter("Use $$remove <number> to remove song from queue");

        event.reply(eb.build());
    }

    public void multiplePages(BlockingQueue<AudioTrack> queue, GuildMusicManager manager, CommandEvent event){

        List<String> list = new ArrayList<>();

        for(AudioTrack track : queue){
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
