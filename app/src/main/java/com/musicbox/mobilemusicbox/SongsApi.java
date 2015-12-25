package com.musicbox.mobilemusicbox;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by fescalona on 12/24/2015.
 */
public interface SongsApi {

    @GET("/api/get-all-songs")
    Call<List<Song>> getAllSongs();


    @GET("/api/song/{id}")
    Call<Song> getSongDetails(@Path("id") String id);




}
