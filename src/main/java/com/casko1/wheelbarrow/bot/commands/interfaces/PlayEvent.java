package com.casko1.wheelbarrow.bot.commands.interfaces;

import net.dv8tion.jda.api.managers.AudioManager;

public interface PlayEvent extends CommonEvent {

    AudioManager getAudioManager();

    String getUrl();

    void setUrl(String url);

    boolean getShuffle();

    boolean isUrl();

    boolean verifyCommandArguments();
}
