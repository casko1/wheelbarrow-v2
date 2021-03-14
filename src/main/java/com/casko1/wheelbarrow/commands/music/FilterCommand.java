package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.commands.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.commands.music.lavaplayer.filters.FilterConfig;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.List;

public class FilterCommand extends Command {

    public FilterCommand(){
        this.name = "filters";
        this.help = "Displays enabled filters";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(VoiceStateCheckUtil.isEligible(event)){
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            List<FilterConfig> configs = musicManager.getFilterConfiguration().filterConfigs;

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.BLUE);
            eb.setTitle("**Filters**");

            StringBuilder sb = new StringBuilder();

            for(FilterConfig config : configs){
                sb.append(String.format("%s: %s\n", config.getName(), config.isEnabled() ? ":white_check_mark:" : ":x:"));
            }

            eb.setDescription(sb.toString());

            event.reply(eb.build());
        }

    }
}
