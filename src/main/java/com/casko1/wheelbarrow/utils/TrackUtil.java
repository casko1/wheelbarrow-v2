package com.casko1.wheelbarrow.utils;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.NotFoundException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.albums.GetAlbumRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

/**
 * Following utility class contains several utilities for
 * processing track requests; thumbnails, titles, etc.
 *
 * Also takes care of refreshing spotify token when passed
 * spotify client
 */

public final class TrackUtil {

    public static String getThumbnail(String query, SpotifyApi spotifyApi, ClientCredentials clientCredentials){
        String res = "attachment";

        Paging<Track> trackPaging = queryTracks(query, spotifyApi, clientCredentials);

        if(trackPaging.getTotal() > 0){
            Image[] images = trackPaging.getItems()[0].getAlbum().getImages();
            res = images.length > 1 ? images[1].getUrl() : images[0].getUrl();
        }

        return res;
    }

    private static String getNewAccessToken(SpotifyApi spotifyApi, ClientCredentials clientCredentials){
        try{
            clientCredentials = spotifyApi.clientCredentials().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e){
            System.out.println(e);
        }
        return clientCredentials.getAccessToken();
    }

    public static String getTitle(String query, SpotifyApi spotifyApi, ClientCredentials clientCredentials){

        String res = "";

        Track track = getTrack(query, spotifyApi, clientCredentials);

        if(track != null){
            res = String.format("%s %s", track.getName(), track.getArtists()[0].getName());
        }

        return res;
    }

    private static Paging<Track> queryTracks(String query, SpotifyApi spotifyApi, ClientCredentials clientCredentials){

        SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query)
                .limit(1)
                .build();

        try{
            return searchTracksRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e){
            spotifyApi.setAccessToken(getNewAccessToken(spotifyApi, clientCredentials));
            return queryTracks(query, spotifyApi, clientCredentials);
        }
    }

    public static Playlist getPlaylist(String query, SpotifyApi spotifyApi, ClientCredentials clientCredentials){
        GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(getSpotifyID(query)).build();

        try{
            return getPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e){
            if(e instanceof NotFoundException) return null;

            spotifyApi.setAccessToken(getNewAccessToken(spotifyApi, clientCredentials));
            return getPlaylist(query, spotifyApi, clientCredentials);
        }
    }


    public static Album getAlbum(String query, SpotifyApi spotifyApi, ClientCredentials clientCredentials){
        GetAlbumRequest getAlbumRequest = spotifyApi.getAlbum(getSpotifyID(query)).build();

        try{
            return getAlbumRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e){
            if(e instanceof NotFoundException) return null;

            spotifyApi.setAccessToken(getNewAccessToken(spotifyApi, clientCredentials));
            return getAlbum(query, spotifyApi, clientCredentials);
        }
    }


    private static Track getTrack(String query, SpotifyApi spotifyApi, ClientCredentials clientCredentials){

        String id = query;

        if(ArgumentsUtil.isUrl(query)){
            id = getSpotifyID(query);
        }

        GetTrackRequest getTrackRequest = spotifyApi.getTrack(id).build();

        try{
            return getTrackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e){
            if(e instanceof NotFoundException) return null;

            spotifyApi.setAccessToken(getNewAccessToken(spotifyApi, clientCredentials));
            return getTrack(query, spotifyApi, clientCredentials);
        }
    }

    private static String getSpotifyID(String link){
        return link.split("/")[4].split("\\?")[0];
    }
}
