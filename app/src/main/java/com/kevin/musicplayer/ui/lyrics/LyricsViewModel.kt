package com.kevin.musicplayer.ui.lyrics

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.gson.Gson
import com.kevin.musicplayer.api.LyricsApi
import com.kevin.musicplayer.api.LyricsResponse
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.model.Lyrics
import com.kevin.musicplayer.repository.LyricsRepository
import com.kevin.musicplayer.util.LyricsApiHelper
import com.squareup.moshi.Json
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LyricsViewModel(application: Application) : BaseViewModel(application) {
	private val lyricsRepository = LyricsRepository(application.applicationContext)
	var lyrics: LiveData<Lyrics?>? = null
	var artist = MutableLiveData<String>()
	var title = MutableLiveData<String>()

	init {
		getLyrics()
	}

	fun getLyrics() {
		if (!artist.value.isNullOrEmpty() && !title.value.isNullOrEmpty()) {
			lyrics = lyricsRepository.getLyrics(artist.value!!, title.value!!)
		}
	}

	fun setData(artist: String, title: String) {
		this.artist.value = artist
		this.title.value = title
	}

}