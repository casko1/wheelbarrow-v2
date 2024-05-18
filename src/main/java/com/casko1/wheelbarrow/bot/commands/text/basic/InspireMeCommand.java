package com.casko1.wheelbarrow.bot.commands.text.basic;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InspireMeCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(InspireMeCommand.class);

    public InspireMeCommand() {
        this.name = "inspireme";
        this.help = "Returns a random inspirational image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        Unirest.get("https://inspirobot.me/api?generate=true")
                .asStringAsync(response -> response
                        .ifSuccess(r -> event.reply(r.getBody()))
                        .ifFailure(e -> {
                            logger.error("An error occurred while executing InspireMe: {}", e.getStatusText());
                            event.reply("An error occurred while executing InspireMe command");
                        }));
    }
}
