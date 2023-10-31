package com.casko1.wheelbarrow.bot.entities;


import com.casko1.wheelbarrow.bot.utils.TimeConverterUtil;
import net.dv8tion.jda.api.entities.Member;

import java.io.File;

public class AdditionalTrackData {

    final private Member requester;
    final private String thumbnail;
    final private String duration;
    private final File defaultImage;

    public AdditionalTrackData(Member requester, String thumbnail, long duration, File defaultImage) {
        this.requester = requester;
        this.thumbnail = thumbnail;
        this.duration = TimeConverterUtil.getMinutesAndSeconds(duration);
        this.defaultImage = defaultImage;
    }

    public Member getRequester() {
        return requester;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDuration() {
        return duration;
    }

    public File getDefaultImage() {
        return defaultImage;
    }
}
