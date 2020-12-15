package com.kevin.musicplayer.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.kevin.musicplayer.R
import com.kevin.musicplayer.api.LyricsApi
import com.kevin.musicplayer.dao.LyricsDao
import com.kevin.musicplayer.database.room.MusicRoomDatabase
import com.kevin.musicplayer.model.Lyrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

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
	suspend fun getLyrics(artist: String, title: String): Flow<Lyrics?> {
		return lyricsDao.getLyrics(artist, title).onEach { lyrics ->
			if (lyrics == null) {
				try {
					val apiLyrics = LyricsApi.create().getLyrics(artist, title)
					if (apiLyrics.lyrics.isBlank()) insertLyrics(Lyrics(title, artist, context.getString(R.string.no_lyrics_found)))
					else insertLyrics(Lyrics(title, artist, apiLyrics.lyrics))
				} catch (ex: Exception) {
					ex.printStackTrace()
					insertLyrics(Lyrics(title, artist, context.getString(R.string.no_lyrics_found)))
				}
			}
		}
	}


	/**
	 * Insert the [Lyrics] in the [MusicRoomDatabase]
	 */
	@WorkerThread
	suspend fun insertLyrics(lyrics: Lyrics) {
		lyricsDao.insert(lyrics)
	}
}