package com.kevin.musicplayer.util

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import com.kevin.musicplayer.model.MusicState
import com.kevin.musicplayer.model.Track

// TODO: Handle incoming calls, headphone remove, different audio focus.
class MediaPlayerManager {
	private var mediaPlayer: MediaPlayer = MediaPlayer()
	var currentTrack: MutableLiveData<MusicState> = MutableLiveData()

	companion object {
		private var mediaPlayerManager: MediaPlayerManager? = null

		fun getInstance(): MediaPlayerManager {
			if (mediaPlayerManager == null) {
				mediaPlayerManager = MediaPlayerManager()
			}
			return mediaPlayerManager!!
		}
	}

	fun playTrack(track: Track) {
		mediaPlayer.reset()
		mediaPlayer.setDataSource(track.data)
		mediaPlayer.prepare()
		mediaPlayer.start()

		currentTrack.value = MusicState(track, MusicState.Companion.MusicState.PLAYING)
	}

	fun pauseTrack() {
		if (currentTrack.value != null) {
			mediaPlayer.pause()
			currentTrack.value = MusicState(currentTrack.value!!.track, MusicState.Companion.MusicState.PAUSING)
		}
	}

	fun resumeTrack() {
		if (currentTrack.value != null) {
			mediaPlayer.start()
			currentTrack.value = MusicState(currentTrack.value!!.track, MusicState.Companion.MusicState.PLAYING)
		}
	}

	fun reset() {
		mediaPlayer.reset()
		currentTrack.value = null
	}

	fun isPlaying(): Boolean = mediaPlayer.isPlaying
}