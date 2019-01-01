package com.kevin.musicplayer.ui.player

import android.app.Application
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.util.MediaSessionConnection

class MusicPlayerViewModel(application: Application) : BaseViewModel(application) {

	private val mediaSessionConnection = MediaSessionConnection.getInstance(application.applicationContext)
	val currentTrack = mediaSessionConnection.currentTrack
	val playBackState = mediaSessionConnection.playBackState

	fun resumeTrack() {
		if (playBackState.value?.state == PlaybackStateCompat.STATE_PAUSED)
			mediaSessionConnection.transportControls.play()
	}

	fun pauseTrack() {
		if (playBackState.value?.state == PlaybackStateCompat.STATE_PLAYING)
			mediaSessionConnection.transportControls.pause()
	}

	fun skipToNext() {
		if (playBackState.value?.state != null && playBackState.value?.state != PlaybackStateCompat.STATE_STOPPED)
			mediaSessionConnection.transportControls.skipToNext()
	}

	fun skipToPrevious() {
		if (playBackState.value?.state != null && playBackState.value?.state != PlaybackStateCompat.STATE_STOPPED)
			mediaSessionConnection.transportControls.skipToPrevious()
	}
}