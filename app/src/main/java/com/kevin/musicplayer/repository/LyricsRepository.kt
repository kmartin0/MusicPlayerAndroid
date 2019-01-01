package com.kevin.musicplayer.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import com.kevin.musicplayer.api.LyricsApi
import com.kevin.musicplayer.api.LyricsResponse
import com.kevin.musicplayer.dao.LyricsDao
import com.kevin.musicplayer.database.room.MusicRoomDatabase
import com.kevin.musicplayer.model.Lyrics
import com.kevin.musicplayer.util.LyricsApiHelper
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LyricsRepository(context: Context) {
	private var lyricsDao: LyricsDao

	init {
		val gameRoomDatabase = MusicRoomDatabase.getDatabase(context)
		lyricsDao = gameRoomDatabase!!.lyricsDao()
	}


	fun getLyrics(artist: String, title: String): LiveData<Lyrics?> {

		val lyrics = lyricsDao.getLyrics(artist, title)

		if (lyrics.value == null) {
			LyricsApi.create().getLyrics(artist, title)
					.enqueue(object : Callback<LyricsResponse> {
						override fun onResponse(call: Call<LyricsResponse>, response: Response<LyricsResponse>) {
							if (response.isSuccessful) {
								insertLyrics(Lyrics(title, artist, response.body()?.lyrics))
							} else {
								insertLyrics(Lyrics(title, artist, LyricsApiHelper.readErrorMessage(response.errorBody()!!)))
							}
						}

						override fun onFailure(call: Call<LyricsResponse>, t: Throwable) {
							insertLyrics(Lyrics(title, artist, t.message))
						}
					})
		}
		return lyrics
	}

	fun insertLyrics(lyrics: Lyrics) {
		doAsync { lyricsDao.insert(lyrics) }
	}
}