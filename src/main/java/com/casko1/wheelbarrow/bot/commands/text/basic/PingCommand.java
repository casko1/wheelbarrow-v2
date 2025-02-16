package com.casko1.wheelbarrow.bot.commands.text.basic;

import com.casko1.wheelbarrow.bot.lib.command.TextCommand;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;

import java.time.temporal.ChronoUnit;

public class PingCommand extends TextCommand {

    public PingCommand() {
        this.name = "ping";
        this.description = "Reports latency to the API";
    }

    @Override
    public void execute(TextCommandEvent event) {
        event.reply("Pinging: ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }
}
