package com.kevin.musicplayer.api

import com.kevin.musicplayer.model.Lyrics
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LyricsApiService {
    @GET("{artist}/{title}")
    fun getLyrics(@Path("artist") artist: String,
                  @Path("title") title: String)
            : Call<Lyrics>
}