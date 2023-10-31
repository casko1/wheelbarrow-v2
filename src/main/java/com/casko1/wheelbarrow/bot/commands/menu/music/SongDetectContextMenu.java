package com.casko1.wheelbarrow.bot.commands.menu.music;

import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.casko1.wheelbarrow.bot.utils.PropertiesUtil;
import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class SongDetectContextMenu extends MessageContextMenu {

    public SongDetectContextMenu() {
        this.name = "detect song";
    }

    @Override
    protected void execute(MessageContextMenuEvent event) {
        event.deferReply().queue();
        String url = ArgumentsUtil.getContentUrl(event.getTarget());

        if(url == null) {
            event.getHook().editOriginal("Cannot find audio to recognize").queue();
            return;
        }

        if(!ArgumentsUtil.isValidVideoType(ArgumentsUtil.getUrlContentType(url))) {
            event.getHook().editOriginal("Unsupported video format or the URL cannot be read from." +
                    " Try uploading the file").queue();
            return;
        }

        recognize(url, (r, f) -> {
            if(f == null || r == null) {
                event.getHook().editOriginal("An error occurred").queue();
            }
            else if(!r.has("track")) {
                event.getHook().editOriginal("No matches found").queue();
            }
            else {
                String title = r.getJSONObject("track").getString("title");
                String author = r.getJSONObject("track").getString("subtitle");
                event.getHook().editOriginal(url + "\nDetected song: **"+ title +
                        "** by **" + author + "**").queue();
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
                callback.accept(null, null);
            }
        }).start();
    }

    private void getTrack(File file, Consumer<JSONObject> callback) throws IOException {
        Unirest.post("https://shazam-core.p.rapidapi.com/v1/tracks/recognize")
                .header("x-rapidapi-host", "shazam-core.p.rapidapi.com")
                .header("x-rapidapi-key", PropertiesUtil.getInstance().getProperty("shazamCoreApi"))
                .field("file", file, "audio/ogg")
                .asJsonAsync(response -> response.ifSuccess(r -> callback.accept(response.getBody().getObject()))
                        .ifFailure(f -> callback.accept(null)));
    }

    private void deleteTempFile(File outFile) {
        new Thread(() -> {
            try  { Thread.sleep( 5000 ); }
            catch (InterruptedException ignored)  {}
            outFile.delete();
        }).start();
    }
}
