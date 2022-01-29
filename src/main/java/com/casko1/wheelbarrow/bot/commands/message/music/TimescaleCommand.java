package com.casko1.wheelbarrow.bot.commands.message.music;

public class TimescaleCommand extends FilterCommand {

    public TimescaleCommand(){
        super("timescale");
        this.name = "timescale";
        this.help = "**Applies timescale filter to current track.** *Example: $$timescale speed 1.2*";
        this.arguments = "<speed | pitch | rate> <number>";
        this.aliases = new String[]{"ts"};
        this.guildOnly = false;
    }
}
