package com.casko1.wheelbarrow;

import com.casko1.wheelbarrow.commands.basic.PingCommand;
import com.casko1.wheelbarrow.commands.basic.WeatherCommand;
import com.casko1.wheelbarrow.commands.music.*;
import com.casko1.wheelbarrow.commands.music.SkipCommand;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;

public class Wheelbarrow {
    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException {

        List<String> config = Files.readAllLines(Paths.get("config.txt"));

        // get configs from config.txt
        String token = config.get(0);
        String ownerId = config.get(1);
        String weatherToken = config.get(2);

        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder()
                .setPrefix("$$")
                .setStatus(OnlineStatus.ONLINE)
                .setOwnerId(ownerId)
                .setActivity(Activity.playing("$$ is my prefix!"));

        Paginator.Builder paginatorBuilder = new Paginator.Builder().setEventWaiter(new EventWaiter());

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
                new LoopCommand()
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
                .addEventListeners(waiter, client.build())
                .build(); //start the bot

    }
}
