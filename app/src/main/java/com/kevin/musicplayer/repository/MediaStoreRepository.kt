package com.kevin.musicplayer.repository

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import com.kevin.musicplayer.database.mediastore.MediaStoreDatabase

class MediaStoreRepository(context: Context) {
	private val mediaStoreDatabase = MediaStoreDatabase(context)

	/**
	 * @return a list of [MediaBrowserCompat.MediaItem] objects from all the tracks in the
	 * MediaStore Database
	 */
	fun getAllTracks(): List<MediaBrowserCompat.MediaItem> {
		return mediaStoreDatabase.getAllTracks()
	}

}


