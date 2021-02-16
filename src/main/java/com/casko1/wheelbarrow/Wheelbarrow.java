package com.casko1.wheelbarrow;

import com.casko1.wheelbarrow.commands.PingCommand;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Wheelbarrow {
    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException {

        List<String> config = Files.readAllLines(Paths.get("config.txt"));

        // get configs from config.txt
        String token = config.get(0);
        String ownerId = config.get(1);

        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder()
                .setPrefix("$")
                .setStatus(OnlineStatus.ONLINE)
                .setOwnerId(ownerId)
                .setActivity(Activity.playing("$ is my prefix!"));

        client.addCommand(new PingCommand());

        JDABuilder.createDefault(token)
                //status while loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                .addEventListeners(waiter, client.build())
                .build(); //start the bot
    }
}
