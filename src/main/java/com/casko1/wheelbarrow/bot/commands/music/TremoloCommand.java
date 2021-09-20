package com.casko1.wheelbarrow.bot.commands.music;

public class TremoloCommand extends FilterCommand {

    public TremoloCommand(){
        super("tremolo");
        this.name = "tremolo";
        this.help = "**Applies tremolo filter to current track.** *Example: $$tremolo depth 1.2*";
        this.arguments = "<freq | depth> <number>";
        this.guildOnly = false;
    }
}
