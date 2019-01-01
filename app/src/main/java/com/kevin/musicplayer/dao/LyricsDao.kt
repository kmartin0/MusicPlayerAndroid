package com.kevin.musicplayer.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kevin.musicplayer.model.Lyrics

@Dao
interface LyricsDao {

	@Insert
	fun insert(lyrics: Lyrics)

	@Query("SELECT * FROM lyrics_table WHERE artist = :artist AND title = :title ")
	fun getLyrics(artist: String, title: String): LiveData<Lyrics?>

}