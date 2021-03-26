package com.casko1.wheelbarrow.commands.music;

public class RotationCommand extends FilterCommand{

    public RotationCommand(){
        super("rotation");
        this.name = "rotation";
        this.help = "**Applies rotating effect to current track.** *Example: $$rotation speed 1.2*";
        this.arguments = "speed <number>";
        this.guildOnly = false;
    }
}
