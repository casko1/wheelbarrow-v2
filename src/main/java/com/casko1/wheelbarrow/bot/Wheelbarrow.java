package com.casko1.wheelbarrow.bot;

import com.casko1.wheelbarrow.bot.commands.hybrid.music.*;
import com.casko1.wheelbarrow.bot.commands.menu.music.SongDetectContextMenu;
import com.casko1.wheelbarrow.bot.commands.slash.basic.WeatherSlashCommand;
import com.casko1.wheelbarrow.bot.commands.slash.music.FilterSlashCommand;
import com.casko1.wheelbarrow.bot.commands.text.basic.InspireMeCommand;
import com.casko1.wheelbarrow.bot.commands.text.basic.PingCommand;
import com.casko1.wheelbarrow.bot.commands.text.music.*;
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

    public static void main(String[] args) throws IllegalArgumentException {
        String token = System.getenv("botToken");

        if (token.equals("replaceWithBotToken")) {
            logger.error("Bot token not provided, make sure to supply it when executing the app");
            return;
        }

        String weatherToken = System.getenv("weatherToken");
        String ownerId = System.getenv("ownerId");

        EventWaiter waiter = new EventWaiter();
        EventWaiter reactionWaiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder()
                .setPrefix("--")
                .setStatus(OnlineStatus.ONLINE)
                .setOwnerId(ownerId)
                .setActivity(Activity.playing("-- is my prefix!"));

        QueuePaginator.Builder paginatorBuilder = new QueuePaginator.Builder().setEventWaiter(reactionWaiter);

        client.addCommands(
                new PingCommand(),
                new JoinCommand(),
                new StopHybridCommand(),
                new SkipHybridCommand(),
                new NowPlayingHybridCommand(),
                new QueueCommand(paginatorBuilder),
                new FilterSettingsHybridCommand(),
                new LoopHybridCommand(),
                new SeekCommand(),
                new RemoveCommand(),
                new ShuffleHybridCommand(),
                new ClearHybridCommand(),
                new PlayHybridCommand(),
                new InspireMeCommand(),
                new PauseHybridCommand()
        );

        client.addSlashCommands(
                new PlayHybridCommand(),
                new FilterSlashCommand(),
                new WeatherSlashCommand(weatherToken),
                new StopHybridCommand(),
                new SkipHybridCommand(),
                new LoopHybridCommand(),
                new ShuffleHybridCommand(),
                new NowPlayingHybridCommand(),
                new FilterSettingsHybridCommand(),
                new ClearHybridCommand(),
                new PauseHybridCommand()
        );

        client.addContextMenu(new SongDetectContextMenu());

        //used for development
        //client.forceGuildOnly("guildId");

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
                .addEventListeners(waiter, reactionWaiter, client.build())
                .build(); //start the bot
    }
}
