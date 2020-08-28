package com.kevin.musicplayer.repository

import androidx.lifecycle.LiveData
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

class LyricsRepository(val context: Context) {
	private var lyricsDao: LyricsDao

	init {
		val musicRoomDataBase = MusicRoomDatabase.getDatabase(context)
		lyricsDao = musicRoomDataBase!!.lyricsDao()
	}

	/**
	 * Get the [Lyrics] object from the [MusicRoomDatabase] for the [artist] and [title].
	 * If the Lyrics are not yet stored in the database an api request will be made which will
	 * then insert the retrieved [Lyrics] in the database.
	 *
	 * @return [LiveData] object which will contain the [Lyrics]
	 */
	fun getLyrics(artist: String, title: String): LiveData<Lyrics?> {
		val lyrics = lyricsDao.getLyrics(artist, title)

		if (lyrics.value == null) {
			LyricsApi.create().getLyrics(artist, title)
					.enqueue(object : Callback<LyricsResponse> {
						override fun onResponse(call: Call<LyricsResponse>, response: Response<LyricsResponse>) {
							if (response.isSuccessful) {
								insertLyrics(Lyrics(title, artist, response.body()?.lyrics))
							} else {
								insertLyrics(Lyrics(title, artist, LyricsApiHelper.readErrorMessage(response.errorBody()!!, context)))
							}
						}

						override fun onFailure(call: Call<LyricsResponse>, t: Throwable) {
							insertLyrics(Lyrics(title, artist, t.message))
						}
					})
		}
		return lyrics
	}

	/**
	 * Insert the [Lyrics] in the [MusicRoomDatabase]
	 */
	fun insertLyrics(lyrics: Lyrics) {
		doAsync { lyricsDao.insert(lyrics) }
	}
}