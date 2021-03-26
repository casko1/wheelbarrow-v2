package com.casko1.wheelbarrow.commands.music;

public class DistortionCommand extends FilterCommand {

    public DistortionCommand(){
        super("distortion");
        this.name = "distortion";
        this.help = "**Distorts the current track.** *Example: $$distortion scale 1.2*";
        this.arguments = "scale <number>";
        this.guildOnly = false;
    }

}
