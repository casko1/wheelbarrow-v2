package com.casko1.wheelbarrow.bot.commands.menu.image;

import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.bot.utils.FaceUtil;
import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import kong.unirest.json.JSONObject;
import net.coobird.thumbnailator.Thumbnails;
import net.dv8tion.jda.api.entities.Message;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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
            event.getHook().editOriginal("Unable to parse the image").queue();
            return;
        }

        try {
            FaceUtil.getLandmarks(url, (v) -> {
                if(v == null) {
                    event.getHook().editOriginal("Unable to parse the image").queue();
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
        String url = ArgumentsUtil.getContentUrl(target);

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
        BufferedImage image = ImageIO.read(new URL(url));

        int[] pupilPos = getPupilCoordinates(landmarks, image.getWidth()/2, image.getHeight()/2);

        BufferedImage overlay = Thumbnails.of(ImageIO.read(getClass().getResourceAsStream("/img/glow.png")))
                .forceSize(image.getWidth()/2, image.getHeight()/2)
                .asBufferedImage();

        Graphics2D g = image.createGraphics();
        g.drawImage(overlay, pupilPos[0], pupilPos[1], null);
        g.drawImage(overlay, pupilPos[2],pupilPos[3], null);
        g.dispose();

        File outFile = File.createTempFile("tmp", ".png");
        ImageIO.write(image, "png", outFile);

        if(outFile.length() > 8000000) {
            event.getHook().editOriginal("File too large").queue();
        }
        else {
            event.getHook().editOriginal(outFile).queue();
        }

        deleteTempFile(outFile);
    }

    private int[] getPupilCoordinates(JSONObject landmarks, int width, int height) {
        int leftPupilX = (int) landmarks.getJSONObject("pupilLeft").getDouble("x") - width/2;
        int leftPupilY = (int) landmarks.getJSONObject("pupilLeft").getDouble("y") - height/2;
        int rightPupilX = (int) landmarks.getJSONObject("pupilRight").getDouble("x") - width/2;
        int rightPupilY = (int) landmarks.getJSONObject("pupilRight").getDouble("y") - height/2;

        return new int[]{leftPupilX, leftPupilY, rightPupilX, rightPupilY};
    }

    private void deleteTempFile(File outFile) {
        new Thread(() -> {
            try  { Thread.sleep( 5000 ); }
            catch (InterruptedException ignored)  {}
            outFile.delete();
        }).start();
    }
}
