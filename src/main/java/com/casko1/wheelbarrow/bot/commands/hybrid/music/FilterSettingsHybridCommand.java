package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.music.lavaplayer.filters.FilterConfig;
import com.casko1.wheelbarrow.bot.utils.VoiceStateCheckUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;

public class FilterSettingsHybridCommand extends HybridCommand {

    public FilterSettingsHybridCommand() {
        this.name = "filters";
        this.description = "Displays enabled filters";
    }

    @Override
    protected void execute(CommonEvent event) {
        if (VoiceStateCheckUtil.isEligible(event, false)) {
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            HashMap<String, FilterConfig> configs = musicManager.getFilterConfiguration().filterConfigs;

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.BLUE);
            eb.setTitle("**Filters**");

            StringBuilder sb = new StringBuilder();

            for (FilterConfig config : configs.values()) {
                sb.append(String.format("%s: %s\n", config.getName(), config.isEnabled() ? ":white_check_mark:" : ":x:"));
            }

            eb.setDescription(sb.toString());

            event.replyEmbed(eb);
        }
    }
}
