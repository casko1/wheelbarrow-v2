package com.casko1.wheelbarrow.entities;


import net.dv8tion.jda.api.entities.Member;
import com.casko1.wheelbarrow.utils.TimeConverterUtil;

import java.io.File;

public class AdditionalTrackData {

    final private Member requester;
    final private String thumbnail;
    final private String duration;
    private File defaultImage = null;

    public AdditionalTrackData(Member requester, String thumbnail, long duration){
        this.requester = requester;
        this.thumbnail = thumbnail;
        this.duration = TimeConverterUtil.getMinutesAndSeconds(duration);
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

    public void setDefaultImage(File defaultImage) {
        this.defaultImage = defaultImage;
    }
}
