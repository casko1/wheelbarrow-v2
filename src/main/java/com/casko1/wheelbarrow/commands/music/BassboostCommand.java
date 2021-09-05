package com.casko1.wheelbarrow.commands.music;

public class BassboostCommand extends FilterCommand{

    public BassboostCommand(){
        super("bassboost");
        this.name = "bassboost";
        this.help = "**Applies timescale filter to current track.** *Example: $$timescale speed 1.2*";
        this.arguments = "<speed | pitch | rate> <number>";
        this.aliases = new String[]{"bb"};
        this.guildOnly = false;
    }
}
