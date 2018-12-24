package com.kevin.musicplayer.util

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import com.kevin.musicplayer.model.Track
// TODO: Handle incoming calls, headphone remove, different audio focus.
class MediaPlayerManager{
	var mediaPlayer: MediaPlayer = MediaPlayer()
	var currentTrack: MutableLiveData<Track?> = MutableLiveData()

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
		currentTrack.value = track
	}

	fun pauseTrack() {
		mediaPlayer.pause()
	}

	fun resumeTrack() {
		mediaPlayer.start()
	}

	fun isPlaying(): Boolean = mediaPlayer.isPlaying
}