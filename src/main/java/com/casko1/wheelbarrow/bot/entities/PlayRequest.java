package com.casko1.wheelbarrow.bot.entities;

import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import net.dv8tion.jda.api.entities.Member;

public class PlayRequest {

    private final PlayEvent event;

    private final String searchString;

    private final String imageSearchString;

    private final boolean isPlaylist;

    private final Member requester;

    public PlayRequest(PlayEvent event, String searchString, String imageSearchString,
                       boolean isPlaylist, Member requester, boolean shuffle){
        this.event = event;
        this.searchString = searchString;
        this.imageSearchString = imageSearchString;
        this.isPlaylist = isPlaylist;
        this.requester = requester;
        this.shuffle = shuffle;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    private final boolean shuffle;

    public PlayEvent getEvent() {
        return event;
    }

    public String getSearchString() {
        return searchString;
    }

    public String getImageSearchString() {
        return imageSearchString;
    }

    public boolean isPlaylist() {
        return isPlaylist;
    }

    public Member getRequester() {
        return requester;
    }
}
