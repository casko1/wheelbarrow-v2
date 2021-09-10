package com.casko1.wheelbarrow.bot.commands.music;

public class KaraokeCommand extends FilterCommand {

    public KaraokeCommand(){
        super("karaoke");
        this.name = "karaoke";
        this.help = "**Applies karaoke filter to current track.** *Example: $$karaoke level 1.2*";
        this.arguments = "<mono|level> <number>";
        this.guildOnly = false;
    }
}
