package com.casko1.wheelbarrow.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(1024); //every 20 ms
        this.lastFrame = new MutableAudioFrame();
        this.lastFrame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        //audioPlayer writes to lastFrame which writes to buffer
        return this.audioPlayer.provide(this.lastFrame); //true if frame is provided
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return this.buffer.flip(); //flips the buffer and sets the position to zero - to read from the start
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
