package com.kevin.musicplayer.ui.player

import android.app.Application
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.util.MediaSessionConnection

class MusicPlayerViewModel(application: Application) : BaseViewModel(application) {

	private val mediaSessionConnection = MediaSessionConnection.getInstance(application.applicationContext)
	val currentTrack = mediaSessionConnection.currentTrack
	val playBackState = mediaSessionConnection.playBackState

	fun resumeTrack() {
		mediaSessionConnection.transportControls.sendCustomAction("ACTION_TOGGLE", null)
	}

	fun pauseTrack() {
		mediaSessionConnection.transportControls.sendCustomAction("ACTION_TOGGLE", null)
	}

	fun skipToNext() {
		mediaSessionConnection.transportControls.skipToNext()
		mediaSessionConnection.transportControls.play()
	}

	fun skipToPrevious() {
		mediaSessionConnection.transportControls.skipToPrevious()
		mediaSessionConnection.transportControls.play()
	}
}