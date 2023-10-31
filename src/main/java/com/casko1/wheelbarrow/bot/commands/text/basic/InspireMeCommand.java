package com.casko1.wheelbarrow.bot.commands.text.basic;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kong.unirest.Unirest;

public class InspireMeCommand extends Command {

    public InspireMeCommand() {
        this.name = "inspireme";
        this.help = "Returns a random inspirational image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        Unirest.get("https://inspirobot.me/api?generate=true").asStringAsync(response -> response.ifSuccess(r -> event.reply(r.getBody()))
                .ifFailure(e -> event.reply("An error occurred. Please try again.")));
    }
}
