package com.casko1.wheelbarrow.bot;

import com.casko1.wheelbarrow.bot.commands.hybrid.music.*;
import com.casko1.wheelbarrow.bot.commands.menu.music.SongDetectContextMenu;
import com.casko1.wheelbarrow.bot.commands.slash.basic.WeatherSlashCommand;
import com.casko1.wheelbarrow.bot.commands.slash.music.FilterSlashCommand;
import com.casko1.wheelbarrow.bot.commands.text.basic.InspireMeCommand;
import com.casko1.wheelbarrow.bot.commands.text.basic.PingCommand;
import com.casko1.wheelbarrow.bot.commands.text.music.*;
import com.casko1.wheelbarrow.bot.lib.handler.EventHandlerBuilder;
import com.casko1.wheelbarrow.bot.music.QueuePaginator;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class Wheelbarrow {
    private static final Logger logger = LoggerFactory.getLogger(Wheelbarrow.class);

    public static void main(String[] args) throws Exception {
        String token = System.getenv("botToken");

        if (token.equals("replaceWithBotToken")) {
            logger.error("Bot token not provided, make sure to supply it when executing the app");
            return;
        }

        String weatherToken = System.getenv("weatherToken");

        EventWaiter waiter = new EventWaiter();
        EventWaiter reactionWaiter = new EventWaiter();

        QueuePaginator.Builder paginatorBuilder = new QueuePaginator.Builder().setEventWaiter(reactionWaiter);

        EventHandlerBuilder handler = new EventHandlerBuilder()
                .setPrefix("--")
                .setDevelopmentGuildId(System.getenv("developmentGuildId"))
                .addTextCommands(
                        new PingCommand(),
                        new JoinCommand(),
                        new QueueCommand(paginatorBuilder),
                        new InspireMeCommand(),
                        new SeekCommand(),
                        new RemoveCommand()
                )
                .addHybridCommands(
                        new PlayHybridCommand(),
                        new ClearHybridCommand(),
                        new FilterSettingsHybridCommand(),
                        new StopHybridCommand(),
                        new SkipHybridCommand(),
                        new LoopHybridCommand(),
                        new ShuffleHybridCommand(),
                        new NowPlayingHybridCommand(),
                        new PauseHybridCommand()
                )
                .addSlashCommands(
                        new FilterSlashCommand(),
                        new WeatherSlashCommand(weatherToken)
                )
                .addContextMenuCommands(new SongDetectContextMenu());

        JDABuilder.createDefault(
                        token,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOJI,
                        CacheFlag.SCHEDULED_EVENTS
                ))
                .enableCache(CacheFlag.VOICE_STATE)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                .addEventListeners(waiter, reactionWaiter, handler.build())
                .build(); //start the bot
    }
}
