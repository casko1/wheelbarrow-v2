package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.commands.events.PlaySlashCommandEvent;
import com.casko1.wheelbarrow.bot.commands.events.PlayTextCommandEvent;
import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.casko1.wheelbarrow.bot.lib.command.HybridCommand;
import com.casko1.wheelbarrow.bot.lib.event.CommonEvent;
import com.casko1.wheelbarrow.bot.lib.event.SlashCommandEvent;
import com.casko1.wheelbarrow.bot.lib.event.TextCommandEvent;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import io.sfrei.tracksearch.clients.youtube.YouTubeClient;
import io.sfrei.tracksearch.exceptions.TrackSearchException;
import io.sfrei.tracksearch.tracks.YouTubeTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class PlayHybridCommand extends HybridCommand {

    private static final Logger logger = LoggerFactory.getLogger(PlayHybridCommand.class);
    private final YouTubeClient searchClient;

    public PlayHybridCommand() {
        this.name = "play";
        this.description = "Plays a song or playlist from specified url or query.";
        this.options = List.of(
                new OptionData(
                        OptionType.STRING, "url-or-search", "URL or name of the song/playlist, optionally select an option from the list", true, true
                ),
                new OptionData(
                        OptionType.BOOLEAN, "shuffle", "Shuffle the playlist", false
                )
        );
        //this.guildOnly = false;
        this.searchClient = new YouTubeClient();
    }

    @Override
    public void execute(TextCommandEvent event) {
        executeCommand(new PlayTextCommandEvent(event));
    }

    @Override
    protected void execute(CommonEvent event) {}

    @Override
    public void execute(SlashCommandEvent event) {
        event.deferReply();
        executeCommand(new PlaySlashCommandEvent(event));
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        super.onAutoComplete(event);
        String query = event.getOption("url-or-search").getAsString();
        if (query.length() <= 3 || ArgumentsUtil.isUrl(query)) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }

        List<Command.Choice> choice = new ArrayList<>();
        List<YouTubeTrack> results = getYouTubeTracks(query);

        if (results.size() > 0) {
            for (int i = 0; i < Math.min(results.size(), 10); i++) {
                YouTubeTrack track = results.get(i);
                String title = track.getTitle();
                String clampedTitle = title.substring(0, Math.min(100, title.length()));
                choice.add(new Command.Choice(clampedTitle, track.getUrl()));
            }

            event.replyChoices(choice).queue();
        } else {
            event.replyChoices(Collections.emptyList()).queue();
        }
    }

    public void executeCommand(PlayEvent event) {
        TextChannel channel = event.getTextChannel();
        GuildVoiceState selfVoiceState = event.getSelfVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You must be in voice channel to use this command");
            return;
        }

        if (!event.verifyCommandArguments()) return;

        if (!event.isUrl()) {
            List<YouTubeTrack> results = getYouTubeTracks(event.getQuery());
            if (results.isEmpty()) {
                event.reply("No results found");
                return;
            }

            event.setUrl(results.get(0).getUrl());
        }

        if (!selfVoiceState.inAudioChannel()) {
            joinVoiceChannel(event, memberVoiceState, channel);
        } else if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.reply("You must be in the same channel as me to use this command");
            return;
        }

        parseAndPlay(event, member);
    }

    private void joinVoiceChannel(PlayEvent event, GuildVoiceState memberVoiceState, TextChannel channel) {

        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioChannel voiceChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(voiceChannel);

        PlayerManager.getInstance().setTextChannel(event.getGuild(), channel);
    }

    private void parseAndPlay(PlayEvent event, Member member) {
        String args = event.getQuery();
        boolean shuffle = event.getShuffle();

        PlayRequest request = new PlayRequest(event, args, "", true, member, shuffle);

        switch (ArgumentsUtil.parseURL(args)) {
            case "spotify.com", "open.spotify.com" -> playSpotify(args, request, member, event);
            case "soundcloud.com" -> playSoundcloud(event, args, member);
            case "" -> event.reply("An error occurred when parsing the URL");
            default -> PlayerManager.getInstance().loadAndPlay(request);
        }
    }

    private void playSpotify(String link, PlayRequest request, Member member, PlayEvent event) {
        switch (ArgumentsUtil.parseSpotifyUrl(link)) {
            case "playlist" -> PlayerManager.getInstance().loadSpotifyTracks("playlist", request);
            case "album" -> PlayerManager.getInstance().loadSpotifyTracks("album", request);
            case "track" -> PlayerManager.getInstance().loadSpotifyTrack(event, link, member);
            default -> event.reply("Spotify URL could not be parsed");
        }
    }

    private void playSoundcloud(PlayEvent event, String link, Member member) {
        String query = link;
        link = "scsearch:" + link;
        PlayRequest request = new PlayRequest(event, link, query, false, member, false);

        PlayerManager.getInstance().loadAndPlay(request);
    }

    private List<YouTubeTrack> getYouTubeTracks(String query) {
        try {
            return searchClient.getTracksForSearch(query).next();
        } catch (TrackSearchException | NullPointerException e) {
            logger.error("An error occurred while searching for tracks: {}", e.toString());
            return Collections.emptyList();
        }
    }
}
