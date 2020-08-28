package com.kevin.musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kevin.musicplayer.model.Lyrics

@Dao
interface LyricsDao {

	@Insert
	fun insert(lyrics: Lyrics)

	@Query("SELECT * FROM lyrics_table WHERE artist = :artist AND title = :title ")
	fun getLyrics(artist: String, title: String): LiveData<Lyrics?>

}