package com.casko1.wheelbarrow.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.time.temporal.ChronoUnit;

public class PingCommand extends Command {

    public PingCommand(){
        this.name = "ping";
        this.help = "reports latency to the API";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.reply("Pinging: ...", m -> {
            long ping = commandEvent.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping  + "ms | Websocket: " + commandEvent.getJDA().getGatewayPing() + "ms").queue();
        });
    }
}
