package com.kevin.musicplayer.ui.lyrics

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.model.Lyrics
import com.kevin.musicplayer.repository.LyricsRepository

class LyricsViewModel(application: Application) : BaseViewModel(application) {
	private val lyricsRepository = LyricsRepository(application.applicationContext)
	var lyrics: LiveData<Lyrics?>? = null
	var artist = MutableLiveData<String>()
	var title = MutableLiveData<String>()

	/**
	 * Gets the lyrics for the [artist] and [title] and stores them in [lyrics]
	 */
	fun getLyrics() {
		if (!artist.value.isNullOrEmpty() && !title.value.isNullOrEmpty()) {
			lyrics = lyricsRepository.getLyrics(artist.value!!, title.value!!)
		}
	}

	/**
	 * Sets the [artist] and [title]
	 */
	fun setData(artist: String, title: String) {
		this.artist.value = artist
		this.title.value = title
	}

}