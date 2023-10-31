package com.casko1.wheelbarrow.bot.commands.hybrid.music;

import com.casko1.wheelbarrow.bot.commands.events.PlaySlashCommandEvent;
import com.casko1.wheelbarrow.bot.commands.events.PlayTextCommandEvent;
import com.casko1.wheelbarrow.bot.commands.interfaces.PlayEvent;
import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class PlayHybridCommand extends SlashCommand {

    private final YouTubeClient searchClient;

    public PlayHybridCommand() {
        this.name = "play";
        this.help = "Plays a song or playlist from specified url or query.";
        this.children = new SlashCommand[]{new Url(), new Search()};
        this.guildOnly = false;
        this.searchClient = new YouTubeClient();
    }

    @Override
    protected void execute(CommandEvent event) {
        executeCommand(new PlayTextCommandEvent(event));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        super.onAutoComplete(event);
        this.children[1].onAutoComplete(event);
    }

    public void executeCommand(PlayEvent event) {


        TextChannel channel = event.getTextChannel();
        GuildVoiceState selfVoiceState = event.getSelfVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You must be in voice channel to use this command.");
            return;
        }

        if (!event.verifyCommandArguments()) return;

        if (!event.isUrl()) {
            List<YouTubeTrack> results = getYouTubeTracks(event.getUrl());
            if (results.isEmpty()) {
                event.reply("No results found");
                return;
            }

            event.setUrl(results.get(0).getUrl());
        }

        if (!selfVoiceState.inAudioChannel()) {
            joinVoiceChannel(event, memberVoiceState, channel);
        } else if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.reply("You must be in the same channel as me to use this command!");
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

        String link = event.getUrl();
        boolean shuffle = event.getShuffle();

        PlayRequest request = new PlayRequest(event, link, "", true, member, shuffle);

        switch (ArgumentsUtil.parseURL(link)) {
            case "spotify.com", "open.spotify.com" -> playSpotify(link, request, member, event);
            case "soundcloud.com" -> playSoundcloud(event, link, member);
            case "" -> event.reply("An error occurred. Please try again.");
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
            return searchClient.getTracksForSearch(query).getTracks();
        } catch (TrackSearchException | NullPointerException e) {
            return Collections.emptyList();
        }
    }

    private class Url extends SlashCommand {

        public Url() {
            this.name = "url";
            this.options = Arrays.asList(
                    new OptionData(
                            OptionType.STRING, "url", "URL of the song/playlist", true
                    ),
                    new OptionData(
                            OptionType.BOOLEAN, "shuffle", "Shuffle the playlist", false
                    )
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().queue();
            PlayHybridCommand.this.executeCommand(new PlaySlashCommandEvent(event, true));
        }
    }

    private class Search extends SlashCommand {

        public Search() {
            this.name = "search";
            this.options = Arrays.asList(
                    new OptionData(
                            OptionType.STRING, "query", "Name of the song/playlist", true, true
                    ),
                    new OptionData(
                            OptionType.BOOLEAN, "shuffle", "Shuffle the playlist", false
                    )
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply().queue();
            PlayHybridCommand.this.executeCommand(new PlaySlashCommandEvent(event, false));
        }

        @Override
        public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
            super.onAutoComplete(event);
            String query = event.getOption("query").getAsString();
            if (query.length() <= 3) {
                event.replyChoices(Collections.emptyList()).queue();
                return;
            }

            List<Command.Choice> choice = new ArrayList<>();
            List<YouTubeTrack> results = getYouTubeTracks(query);

            if (results.size() > 0) {
                for (int i = 0; i < Math.min(results.size(), 10); i++) {
                    YouTubeTrack track = results.get(i);
                    choice.add(new Command.Choice(track.getTitle(), track.getUrl()));
                }

                event.replyChoices(choice).queue();
            } else {
                event.replyChoices(Collections.emptyList()).queue();
            }
        }
    }
}
