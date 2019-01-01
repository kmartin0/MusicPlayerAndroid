package com.kevin.musicplayer.ui.lyrics

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.gson.Gson
import com.kevin.musicplayer.api.LyricsApi
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.model.Lyrics
import com.kevin.musicplayer.util.LyricsApiHelper
import com.squareup.moshi.Json
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LyricsViewModel(application: Application) : BaseViewModel(application) {
    val lyrics = MutableLiveData<String>()
    var artist = MutableLiveData<String>()
    var title = MutableLiveData<String>()

    init {
        getLyrics()
    }

    fun getLyrics() {
        if (!artist.value.isNullOrEmpty() && !title.value.isNullOrEmpty()) {
            val apiService = LyricsApi.create()
            apiService.getLyrics(artist.value!!, title.value!!)
                    .enqueue(object : Callback<Lyrics> {
                        override fun onFailure(call: Call<Lyrics>, t: Throwable) {
                            lyrics.value = t.message
                        }

                        override fun onResponse(call: Call<Lyrics>, response: Response<Lyrics>) {
                            if (response.isSuccessful) {
                                lyrics.value = response.body()?.text
                            } else {
                                lyrics.value = LyricsApiHelper.readErrorMessage(response.errorBody()!!)
                            }
                        }
                    })
        }
    }

    fun setData(artist: String, title: String) {
        this.artist.value = artist
        this.title.value = title
    }

}