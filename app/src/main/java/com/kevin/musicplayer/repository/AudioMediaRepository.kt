package com.kevin.musicplayer.repository

import android.content.Context
import com.kevin.musicplayer.database.mediastore.MediaStoreDatabase
import com.kevin.musicplayer.database.room.MusicRoomDatabase

class AudioMediaRepository(context: Context) {
	private val mediaStoreDatabase = MediaStoreDatabase(context)
//	private var trackDao: TrackDao

	init {
		val gameRoomDatabase = MusicRoomDatabase.getDatabase(context)
//		trackDao = gameRoomDatabase!!.trackDao()
	}

	/**
	 * @return LiveData<List<Track>> containing the Tracks on the device.
	 */
//	fun getAllTracks(): LiveData<List<Track>> {
//		val trackLiveData = MutableLiveData<List<Track>>()
//		doAsync {
//			val tracks = mediaStoreDatabase.getAllTracks()
//			uiThread { trackLiveData.value = tracks }
//		}
//		return trackLiveData
//	}

//	/**
//	 * @return LiveData<List<Track>> containing the Tracks on the device.
//	 */
//	fun getAllTracks(): LiveData<List<Track>> {
//		doAsync {
//			trackDao.insert(mediaStoreDatabase.getAllTracks())
//		}
//		return trackDao.getTracks()
//	}
}


