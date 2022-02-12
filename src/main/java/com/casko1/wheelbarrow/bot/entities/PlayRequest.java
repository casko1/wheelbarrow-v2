package com.casko1.wheelbarrow.bot.entities;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Member;

public class PlayRequest {

    private final SlashCommandEvent event;

    private final String searchString;

    private final String imageSearchString;

    private final boolean isPlaylist;

    private final Member requester;

    public PlayRequest(SlashCommandEvent event, String searchString, String imageSearchString,
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

    public SlashCommandEvent getEvent() {
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
