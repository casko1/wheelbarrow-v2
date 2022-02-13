package com.casko1.wheelbarrow.bot;

import com.casko1.wheelbarrow.bot.commands.menu.image.WokeContextMenu;
import com.casko1.wheelbarrow.bot.commands.menu.music.SongDetectContextMenu;
import com.casko1.wheelbarrow.bot.commands.slash.basic.WeatherCommand;
import com.casko1.wheelbarrow.bot.commands.slash.music.FilterCommand;
import com.casko1.wheelbarrow.bot.commands.slash.music.PlayCommand;
import com.casko1.wheelbarrow.bot.commands.text.basic.PingCommand;
import com.casko1.wheelbarrow.bot.commands.text.music.*;
import com.casko1.wheelbarrow.bot.music.QueuePaginator;
import com.casko1.wheelbarrow.bot.server.ApiMessageServer;
import com.casko1.wheelbarrow.bot.utils.PropertiesUtil;
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
import java.io.IOException;
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
        String enableApi = config.getProperty("enableApi");
        String enableFaceApi = config.getProperty("enableFaceApi");
        String enableSongDetection = config.getProperty("enableSongDetection");

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
                new StopCommand(),
                new SkipCommand(),
                new NowPlayingCommand(),
                new QueueCommand(paginatorBuilder),
                new FilterSettingsCommand(),
                new LoopCommand(),
                new SeekCommand(),
                new RemoveCommand(),
                new ShuffleCommand(),
                new ClearCommand()
                );

        client.addSlashCommands(
                new PlayCommand(),
                new FilterCommand(),
                new WeatherCommand(weatherToken));


        if(enableFaceApi.equals("true")){
            client.addContextMenu(new WokeContextMenu());
        }

        if(enableSongDetection.equals("true")){
            client.addContextMenu(new SongDetectContextMenu());
        }

        //used for development
        //client.forceGuildOnly("597206662025314324");

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
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                .addEventListeners(waiter, reactionWaiter, client.build())
                .build(); //start the bot


        if(enableApi.equals("true")){
            new ApiMessageServer();
        }

    }
}
