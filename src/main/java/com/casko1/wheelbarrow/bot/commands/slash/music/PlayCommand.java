package com.casko1.wheelbarrow.bot.commands.slash.music;

import com.casko1.wheelbarrow.bot.entities.PlayRequest;
import com.casko1.wheelbarrow.bot.music.lavaplayer.PlayerManager;
import com.casko1.wheelbarrow.bot.utils.ArgumentsUtil;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import io.sfrei.tracksearch.clients.youtube.YouTubeClient;
import io.sfrei.tracksearch.exceptions.TrackSearchException;
import io.sfrei.tracksearch.tracks.YouTubeTrack;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
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
public class PlayCommand extends SlashCommand {

    private final YouTubeClient searchClient;

    public PlayCommand(){
        this.name = "play";
        this.help = "Plays a song or playlist from specified url or query.";
        this.children = new SlashCommand[]{new Url(), new Search()};
        this.guildOnly = false;
        this.searchClient = new YouTubeClient();
    }

    @Override
    protected void execute(SlashCommandEvent event) {
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        super.onAutoComplete(event);
        this.children[1].onAutoComplete(event);
    }

    public void executeCommand(SlashCommandEvent event, boolean isUrl) {
        event.deferReply().queue();

        TextChannel channel = event.getTextChannel();
        GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();

        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()){
            event.reply("You must be in voice channel to use this command.").queue();
            return;
        }

        if(!selfVoiceState.inAudioChannel()){
            joinVoiceChannel(event, memberVoiceState, channel);
        }
        else if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            event.reply("You must be in the same channel as me to use this command!").queue();
            return;
        }

        if(isUrl && !ArgumentsUtil.isUrl(event.getOption("url").getAsString())) {
            event.getHook().editOriginal("You must provide an URL when using this command").queue();
            return;
        }

        parseAndPlay(event, member, isUrl);
    }

    private void joinVoiceChannel(SlashCommandEvent event, GuildVoiceState memberVoiceState, TextChannel channel){

        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioChannel voiceChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(voiceChannel);

        PlayerManager.getInstance().setTextChannel(event.getGuild(), channel);
    }

    private void parseAndPlay(SlashCommandEvent event, Member member, boolean isUrl){

        String link = isUrl ? event.getOption("url").getAsString() : event.getOption("query").getAsString();
        boolean shuffle = event.hasOption("shuffle") && event.getOption("shuffle").getAsBoolean();

        PlayRequest request = new PlayRequest(event, link, "", true, member, shuffle);

        switch(ArgumentsUtil.parseURL(link)){
            case "spotify.com", "open.spotify.com" -> playSpotify(link, request, member, event);
            case "soundcloud.com" -> playSoundcloud(event, link, member);
            case "" -> event.reply("An error occurred. Please try again.").queue();
            default -> PlayerManager.getInstance().loadAndPlay(request);
        }
    }

    private void playSpotify(String link, PlayRequest request, Member member, SlashCommandEvent event){
        switch (ArgumentsUtil.parseSpotifyUrl(link)) {
            case "playlist" -> PlayerManager.getInstance().loadSpotifyTracks("playlist", request);
            case "album" -> PlayerManager.getInstance().loadSpotifyTracks("album", request);
            case "track" -> PlayerManager.getInstance().loadSpotifyTrack(event, link, member);
            default -> event.reply("Spotify URL could not be parsed").queue();
        }
    }

    private void playSoundcloud(SlashCommandEvent event, String link, Member member){
        String query = link;
        link = "scsearch:" + link;
        PlayRequest request = new PlayRequest(event, link, query, false, member, false);

        PlayerManager.getInstance().loadAndPlay(request);
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
            PlayCommand.this.executeCommand(event, true);
        }
    }

    private class Search extends SlashCommand {

        public Search() {
            this.name="search";
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
            PlayCommand.this.executeCommand(event, false);
        }

        @Override
        public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
            super.onAutoComplete(event);
            String query = event.getOption("query").getAsString();
            if(query.length() <= 3) {
                event.replyChoices(Collections.emptyList()).queue();
                return;
            }

            try {
                List<Command.Choice> choice = new ArrayList<>();
                List<YouTubeTrack> results = searchClient.getTracksForSearch(query).getTracks();

                for(int i = 0; i < Math.min(results.size(), 10); i++) {
                    YouTubeTrack track = results.get(i);
                    choice.add(new Command.Choice(track.getTitle(), track.getUrl()));
                }

                event.replyChoices(choice).queue();
            } catch (TrackSearchException e) {
                event.replyChoices(Collections.emptyList()).queue();
            }
        }
    }
}
