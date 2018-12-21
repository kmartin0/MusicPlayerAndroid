package com.kevin.musicplayer.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kevin.musicplayer.model.Track

@Dao
interface TrackDao {
	@Insert
	fun insert(track: Track)

	@Insert
	fun insert(track: List<Track>)

	@Delete
	fun delete(track: Track)

	@Update
	fun update(track: Track)

	@Query("SELECT * from track_table")
	fun getTracks(): LiveData<List<Track>>

	@Query("DELETE from track_table")
	fun clearTracks()
}
