package com.casko1.wheelbarrow.bot.commands.image;

import com.casko1.wheelbarrow.bot.utils.FaceUtil;
import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.entities.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

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
            event.respond("Could not parse the image");
            return;
        }

        try {
            Consumer<JSONObject> callback = v -> {
                System.out.println("Test");
            };

            FaceUtil.getLandmarks(url, callback);

        } catch (IOException e) {
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
}
