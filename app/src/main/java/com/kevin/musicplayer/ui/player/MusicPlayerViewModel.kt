package com.kevin.musicplayer.ui.player

import android.app.Application
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.util.MediaPlayerManager
import com.kevin.musicplayer.util.SingleLiveEvent

class MusicPlayerViewModel(application: Application) : BaseViewModel(application) {
	//val mediaPlayerManager = MediaPlayerManager.getInstance()
	val resumeEvent = SingleLiveEvent<Any>()
	val pauseEvent = SingleLiveEvent<Any>()

	fun resumeTrack() {
		//	if (mediaPlayerManager.currentTrack.value != null) {
		//		mediaPlayerManager.resumeTrack()
		resumeEvent.call()
		//	}
	}

	fun pauseTrack() {
		//	mediaPlayerManager.pauseTrack()
		pauseEvent.call()
	}
}