package com.casko1.wheelbarrow.bot.commands.menu.music;

import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class SongDetectContextMenu extends MessageContextMenu {

    private static final Logger logger = LoggerFactory.getLogger(SongDetectContextMenu.class);

    public SongDetectContextMenu() {
        this.name = "detect song";
    }

    @Override
    protected void execute(MessageContextMenuEvent event) {
        event.deferReply().queue();
        String url = ArgumentsUtil.getContentUrl(event.getTarget());

        if (url == null) {
            event.getHook().editOriginal("Cannot find audio to recognize").queue();
            return;
        }
        String type = ArgumentsUtil.getUrlContentType(url);

        if (!ArgumentsUtil.isValidVideoType(type)) {
            event.getHook().editOriginal("Unsupported video format or the URL cannot be read from." +
                    " Try uploading the file").queue();
            return;
        }

        recognize(url, (r, f) -> {
            if (f == null || r == null) {
                event.getHook().editOriginal("An error occurred").queue();
            } else if (!r.has("result")) {
                event.getHook().editOriginal("No matches found").queue();
            } else {
                JSONObject res = r.getJSONObject("result");
                String title = res.getJSONObject("track").getString("title");
                String author = res.getJSONObject("track").getString("subtitle");
                event.getHook().editOriginal(url + "\nDetected song: **" + title + "** by **" + author + "**").queue();
            }
            deleteTempFile(f);
        });
    }

    private void recognize(String url, BiConsumer<JSONObject, File> callback) {
        new Thread(() -> {
            try {
                File out;
                out = File.createTempFile("out", ".ogg");
                FFmpeg ffmpeg = new FFmpeg();
                FFprobe ffprobe = new FFprobe();
                FFmpegBuilder builder = new FFmpegBuilder()
                        .setInput(url)
                        .addOutput(out.getAbsolutePath())
                        .setStartOffset(0, TimeUnit.SECONDS)
                        .setAudioCodec("libvorbis")
                        .addExtraArgs("-vn")
                        .setDuration(5, TimeUnit.SECONDS)
                        .done();

                FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
                executor.createJob(builder).run();

                getTrack(out, (r) -> callback.accept(r, out));
            } catch (IOException e) {
                logger.error("An error occurred while processing audio file with ffmpeg: {}", e.toString());
                callback.accept(null, null);
            }
        }).start();
    }

    private void getTrack(File file, Consumer<JSONObject> callback) throws IOException {
        Unirest.post("http://localhost:5000/detect/")
                .field("file", file, "audio/ogg")
                .asJsonAsync(response -> processApiResponse(response, callback));
    }

    private void processApiResponse(HttpResponse<JsonNode> response, Consumer<JSONObject> callback) {
        if (response.isSuccess()) {
            callback.accept(response.getBody().getObject());
        } else {
            logger.error("An error occurred while calling shazam API: {}", response.getStatusText());
            callback.accept(null);
        }
    }

    private void deleteTempFile(File outFile) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error("An error occurred while deleting temporary file: {}", e.toString());
            }
            outFile.delete();
        }).start();
    }
}
