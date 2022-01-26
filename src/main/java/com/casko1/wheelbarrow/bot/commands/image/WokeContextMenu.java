package com.casko1.wheelbarrow.bot.commands.image;

import com.casko1.wheelbarrow.bot.utils.FaceUtil;
import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class WokeContextMenu extends MessageContextMenu {

    public WokeContextMenu() {
        this.name = "woke";
    }

    @Override
    protected void execute(MessageContextMenuEvent event) {
        event.deferReply().queue();
        Message target = event.getInteraction().getTarget();
        String url = parseImage(target);

        if(url == null) {
            event.getHook().editOriginal("Unable to parse the image.").queue();
            return;
        }

        try {
            FaceUtil.getLandmarks(url, (v) -> {
                if(v == null) {
                    event.getHook().editOriginal("Unable to parse the image.").queue();
                }
                else {
                    try {
                        overlayImage(v, event, url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            event.getHook().editOriginal("An error occurred").queue();
            e.printStackTrace();
        }
    }

    private String parseImage(Message target) {
        String url = null;

        if(target.getAttachments().size() > 0) url = target.getAttachments().get(0).getUrl();
        if(target.getEmbeds().size() > 0) url = target.getEmbeds().get(0).getUrl();

        if(url == null) return null;

        BufferedImage i;

        try {
            i = ImageIO.read(new URL(url));
            if(i == null) return null;
        } catch (IOException | IllegalArgumentException  e) {
            return null;
        }

        return url;
    }

    private void overlayImage(JSONObject landmarks, MessageContextMenuEvent event, String url) throws IOException {
        event.getHook().editOriginal("Processing the image").queue();

        Mat im = imread(new BytePointer(IOUtils.toByteArray(new URL(url))));

        File h = File.createTempFile("tmp", "jpg");

        imwrite(h.getAbsolutePath(), im);

        event.getHook().editOriginal(h).queue();
    }
}
