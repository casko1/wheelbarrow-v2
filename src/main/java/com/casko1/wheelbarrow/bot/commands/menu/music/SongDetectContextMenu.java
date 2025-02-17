package com.casko1.wheelbarrow.bot.commands.menu.music;

import com.casko1.wheelbarrow.bot.lib.command.ContextMenuCommand;
import com.casko1.wheelbarrow.bot.lib.event.ContextMenuEvent;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
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


public class SongDetectContextMenu extends ContextMenuCommand {

    private static final Logger logger = LoggerFactory.getLogger(SongDetectContextMenu.class);

    public SongDetectContextMenu() {
        this.name = "detect song";
    }

    @Override
    public void execute(ContextMenuEvent event) {
        event.deferReply();
        String url = ArgumentsUtil.getContentUrl(event.getTarget());

        if (url == null) {
            event.reply("Cannot find audio to recognize");
            return;
        }
        String type = ArgumentsUtil.getUrlContentType(url);

        if (!ArgumentsUtil.isValidVideoType(type)) {
            event.reply("Unsupported video format or the URL cannot be read from." +
                    " Try uploading the file");
            return;
        }

        recognize(url, (r, f) -> {
            if (f == null || r == null) {
                event.reply("An error occurred");
            } else if (!r.has("result") || (r.has("matches") && r.getInt("matches") == 0)) {
                event.reply("No matches found");
            } else {
                JSONObject res = r.getJSONObject("result");
                String title = res.getJSONObject("track").getString("title");
                String author = res.getJSONObject("track").getString("subtitle");
                event.reply(url + "\nDetected song: **" + title + "** by **" + author + "**");
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
