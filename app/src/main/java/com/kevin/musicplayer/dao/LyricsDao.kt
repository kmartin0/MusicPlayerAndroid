package com.kevin.musicplayer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevin.musicplayer.model.Lyrics
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricsDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(lyrics: Lyrics)

	@Query("SELECT * FROM lyrics_table WHERE artist = :artist AND title = :title ")
	fun getLyrics(artist: String, title: String): Flow<Lyrics?>

}