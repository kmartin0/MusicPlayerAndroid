package com.kevin.musicplayer.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kevin.musicplayer.model.QueueTrack
import android.arch.persistence.room.Transaction


@Dao
interface QueueDao {
//	@Insert
//	fun insert(queueTrack: QueueTrack)

	@Insert
	fun insert(queueTracks: List<QueueTrack>)

	@Delete
	fun delete(queueTrack: QueueTrack)

	@Update
	fun update(queueTrack: QueueTrack)

	@Query("SELECT * from queue_table")
	fun getQueue(): LiveData<List<QueueTrack>>

	@Query("SELECT * from queue_table where currentTrack == 1")
	fun getCurrentTrack(): LiveData<QueueTrack>

	@Query("DELETE from queue_table")
	fun deleteAll()

	@Transaction
	fun insertNewQueue(queueTracks: List<QueueTrack>) {
		deleteAll()
		insert(queueTracks)
	}
}