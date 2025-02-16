package com.casko1.wheelbarrow.bot.commands.text.basic;

import com.casko1.wheelbarrow.bot.lib.command.TextCommand;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InspireMeCommand extends TextCommand {

    private static final Logger logger = LoggerFactory.getLogger(InspireMeCommand.class);

    public InspireMeCommand() {
        this.name = "inspireme";
        this.description = "Returns a random inspirational image";
    }

    @Override
    public void execute(TextCommandEvent event) {
        Unirest.get("https://inspirobot.me/api?generate=true")
                .asStringAsync(response -> response
                        .ifSuccess(r -> event.reply(r.getBody()))
                        .ifFailure(e -> {
                            logger.error("An error occurred while executing InspireMe: {}", e.getStatusText());
                            event.reply("An error occurred while executing InspireMe command");
                        }));
    }
}
