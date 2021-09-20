package com.casko1.wheelbarrow.bot.commands.music;

public class BassboostCommand extends FilterCommand{

    public BassboostCommand(){
        super("bassboost");
        this.name = "bassboost";
        this.help = "**Applies bass boost filter to current track.** *Example: $$bassboost gain 4*";
        this.arguments = "gain <number> [0-10]";
        this.aliases = new String[]{"bb"};
        this.guildOnly = false;
    }
}
