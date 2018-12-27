package com.kevin.musicplayer.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import com.kevin.musicplayer.dao.QueueDao
import com.kevin.musicplayer.database.room.MusicRoomDatabase
import com.kevin.musicplayer.model.QueueTrack
import org.jetbrains.anko.doAsync

class QueueRepository(context: Context) {
	private var queueDao: QueueDao

	init {
		val gameRoomDatabase = MusicRoomDatabase.getDatabase(context)
		queueDao = gameRoomDatabase!!.queueDao()
	}

	fun insert(queueTracks: List<QueueTrack>) {
		doAsync { queueDao.insert(queueTracks) }
	}

	fun insertNewQueue(queueTracks: List<QueueTrack>) {
		doAsync { queueDao.insertNewQueue(queueTracks) }
	}

	fun getQueue(): LiveData<List<QueueTrack>> {
		return queueDao.getQueue()
	}

	fun deleteTrack(queueTrack: QueueTrack) {
		doAsync { queueDao.delete(queueTrack) }
	}

	fun updateTrack(queueTrack: QueueTrack) {
		doAsync { queueDao.update(queueTrack) }
	}

	fun clearQueue() {
		doAsync { queueDao.deleteAll() }
	}

	fun getCurrentTrack(): LiveData<QueueTrack> {
		return queueDao.getCurrentTrack()
	}
}