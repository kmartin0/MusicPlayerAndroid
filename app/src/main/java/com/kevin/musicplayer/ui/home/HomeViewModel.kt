package com.kevin.musicplayer.ui.home

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.util.MediaSessionConnection

class HomeViewModel(application: Application) : BaseViewModel(application) {

	private val mediaSessionConnection = MediaSessionConnection.getInstance(application.applicationContext)
	val trackList = mediaSessionConnection.mediaItems

	fun play(mediaItem: MediaBrowserCompat.MediaItem) {
		mediaSessionConnection.mediaController.addQueueItem(mediaItem.description)
		mediaSessionConnection.transportControls.play()
	}

}
