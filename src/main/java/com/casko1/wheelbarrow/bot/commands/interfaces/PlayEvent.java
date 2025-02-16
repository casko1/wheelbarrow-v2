package com.casko1.wheelbarrow.bot.commands.interfaces;

import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;

public interface PlayEvent extends CommonEvent {

    String getQuery();

    void setUrl(String url);

    boolean getShuffle();

    boolean isUrl();

    boolean verifyCommandArguments();
}
