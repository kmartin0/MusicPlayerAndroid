package com.kevin.musicplayer.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LyricsApiService {
    @GET("{artist}/{title}")
    fun getLyrics(@Path("artist") artist: String,
                  @Path("title") title: String)
            : Call<LyricsResponse>
}