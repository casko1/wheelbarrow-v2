package com.casko1.wheelbarrow.commands.music;

import com.casko1.wheelbarrow.music.lavaplayer.GuildMusicManager;
import com.casko1.wheelbarrow.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.utils.TimeConverterUtil;
import com.casko1.wheelbarrow.utils.VoiceStateCheckUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class SeekCommand extends Command {

    public SeekCommand(){
        this.name = "seek";
        this.help = "Seeks current track to specified timestamp (in seconds)";
        this.arguments = "<timestamp in seconds>";
        this.guildOnly = false;
    }


    @Override
    protected void execute(CommandEvent event) {
        if(VoiceStateCheckUtil.isEligible(event, false)){
            GuildMusicManager guildMusicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            AudioTrack currentTrack = guildMusicManager.audioPlayer.getPlayingTrack();

            String[] args = event.getArgs().split(" ");

            if(currentTrack.isSeekable() && ArgumentsUtil.isInteger(args[0])){
                int timeStamp = Integer.parseInt(args[0]);

                currentTrack.setPosition((long) timeStamp * 1000);

                sendTimestampMessage(event, timeStamp, currentTrack.getDuration());
            }
            else{
                event.reply("Track cannot be seeked or the command was used incorrectly");
            }
        }
    }


    private void sendTimestampMessage(CommandEvent event, int timeStamp, long trackDuration){

        if(timeStamp <= 0){
            event.reply("Seeking current track to 0:00");
        }
        else if(timeStamp * 1000 > trackDuration){
            event.reply("Seeking beyond song duration. Skipping current track");
        }
        else{
            event.reply(String.format("Seeking current track to %s",
                    TimeConverterUtil.getMinutesAndSeconds(timeStamp * 1000)));
        }
    }
}
