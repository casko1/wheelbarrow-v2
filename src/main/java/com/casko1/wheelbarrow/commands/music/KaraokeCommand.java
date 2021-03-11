package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.FilterConfiguration;
import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class KaraokeCommand extends Command {

    public KaraokeCommand(){
        this.name = "karaoke";
        this.help = "Applies karaoke effect to current track";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        FilterConfiguration config = guildMusicManager.getFilterConfiguration();

        if(!config.karaoke.isEnabled()){
            config.karaoke.enable();
            config.karaoke.setLevel(Float.parseFloat(event.getArgs()));
            guildMusicManager.setFilters();
        }
        else{
            config.karaoke.setLevel(Float.parseFloat(event.getArgs()));
            config.karaoke.updateFilter();
        }
    }
}
