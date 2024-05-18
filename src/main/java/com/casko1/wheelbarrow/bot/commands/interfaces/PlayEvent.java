package com.casko1.wheelbarrow.bot.commands.interfaces;

public interface PlayEvent extends CommonEvent {

    String getArgs();

    void setUrl(String url);

    boolean getShuffle();

    boolean isUrl();

    boolean verifyCommandArguments();
}
