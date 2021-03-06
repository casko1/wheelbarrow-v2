package com.casko1.wheelbarrow;

import com.casko1.wheelbarrow.commands.basic.PingCommand;
import com.casko1.wheelbarrow.commands.basic.WeatherCommand;
import com.casko1.wheelbarrow.commands.music.*;
import com.casko1.wheelbarrow.commands.music.SkipCommand;
import com.casko1.wheelbarrow.music.QueuePaginator;
import com.casko1.wheelbarrow.utils.PropertiesUtil;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.EnumSet;
import java.util.Properties;

public class Wheelbarrow {
    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException {

        Logger logger = LoggerFactory.getLogger(Wheelbarrow.class);

        Properties config = PropertiesUtil.getProperties();

        if(config == null) {
            logger.info("Generating properties file. Please enter your bot token in wheelbarrow.properties file!");
            return;
        }

        if(config.getProperty("botToken").equals("replaceWithBotToken")) {
            logger.info("Detected default botToken value. Please enter your bot token in wheelbarrow.properties file!");
            return;
        }

        String token = config.getProperty("botToken");
        String ownerId = config.getProperty("ownerId");
        String weatherToken = config.getProperty("weatherToken");

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
                new WeatherCommand(weatherToken),
                new JoinCommand(),
                new PlayCommand(),
                new StopCommand(),
                new SkipCommand(),
                new NowPlayingCommand(),
                new TimescaleCommand(),
                new KaraokeCommand(),
                new DistortionCommand(),
                new TremoloCommand(),
                new RotationCommand(),
                new QueueCommand(paginatorBuilder),
                new FilterSettingsCommand(),
                new LoopCommand(),
                new SeekCommand(),
                new RemoveCommand(),
                new ShuffleCommand()
                );

        JDABuilder.createDefault(
                token,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
                )
                .disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOTE
                ))
                .enableCache(CacheFlag.VOICE_STATE)
                //status while loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                .addEventListeners(waiter, reactionWaiter, client.build())
                .build(); //start the bot

    }
}
