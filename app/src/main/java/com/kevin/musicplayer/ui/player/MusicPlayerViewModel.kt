package com.kevin.musicplayer.ui.player

import android.app.Application
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.util.MediaPlayerManager
import com.kevin.musicplayer.util.SingleLiveEvent

class MusicPlayerViewModel(application: Application) : BaseViewModel(application) {
	val mediaPlayerManager = MediaPlayerManager.getInstance()
	val resumeEvent = SingleLiveEvent<Any>()
	val pauseEvent = SingleLiveEvent<Any>()

	fun resumeTrack() {
		if (mediaPlayerManager.queueTracks.value != null) {
			resumeEvent.call()
		}
	}

	fun pauseTrack() {
		if (mediaPlayerManager.queueTracks.value != null) {
			pauseEvent.call()
		}
	}
}