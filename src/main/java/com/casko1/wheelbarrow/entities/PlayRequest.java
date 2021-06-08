package com.casko1.wheelbarrow.entities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class PlayRequest {

    public PlayRequest(TextChannel textChannel, String searchString, String imageSearchString,
                       boolean isPlaylist, Member requester, boolean shuffle){
        this.textChannel = textChannel;
        this.searchString = searchString;
        this.imageSearchString = imageSearchString;
        this.isPlaylist = isPlaylist;
        this.requester = requester;
        this.shuffle = shuffle;
    }

    private final TextChannel textChannel;

    private final String searchString;

    private final String imageSearchString;

    private final boolean isPlaylist;

    private final Member requester;

    public boolean isShuffle() {
        return shuffle;
    }

    private final boolean shuffle;

    public TextChannel getTextChannel() {
        return textChannel;
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
