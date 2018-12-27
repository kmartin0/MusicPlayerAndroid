package com.kevin.musicplayer.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import android.util.Log
import com.kevin.musicplayer.model.MusicState
import com.kevin.musicplayer.model.QueueTrack
import com.kevin.musicplayer.model.Track

// TODO: Handle incoming calls, headphone remove, different audio focus.
class MediaPlayerManager : MediaPlayer.OnCompletionListener {
	private var mediaPlayer: MediaPlayer = MediaPlayer()
	var queueTracks: MutableLiveData<ArrayList<QueueTrack>?> = MutableLiveData()
	//var currentTrackIndex: MutableLiveData<Int?> = MutableLiveData()
	//var currentTrack: MutableLiveData<QueueTrack> = MutableLiveData()

	companion object {
		private var mediaPlayerManager: MediaPlayerManager? = null

		fun getInstance(): MediaPlayerManager {
			if (mediaPlayerManager == null) {
				mediaPlayerManager = MediaPlayerManager()
			}
			return mediaPlayerManager!!
		}
	}

	init {
		mediaPlayer.setOnCompletionListener(this)
	}

	fun initializeQueue(queueTracks: ArrayList<QueueTrack>) {
		this.queueTracks.value = ArrayList(queueTracks)
	}

	fun playTrack() {
		queueTracks.value?.let { queueTracks ->
			mediaPlayer.reset()
			mediaPlayer.setDataSource(queueTracks[0].track.data)
			mediaPlayer.prepare()
			mediaPlayer.start()
			updateCurrentTrackState(MusicState.PLAYING)
		}
	}

	fun toggle() {
		if (queueTracks.value != null) {
			if (mediaPlayer.isPlaying) pauseTrack()
			else resumeTrack()
		}
	}

	private fun pauseTrack() {
		if (queueTracks.value != null) {
			mediaPlayer.pause()
			updateCurrentTrackState(MusicState.PAUSING)
		}
	}

	private fun resumeTrack() {
		if (queueTracks.value != null) {
			mediaPlayer.start()
			updateCurrentTrackState(MusicState.PLAYING)
		}
	}

	private fun updateCurrentTrackState(newState: MusicState) {
		if (queueTracks.value != null && queueTracks.value!!.isNotEmpty()) {
			queueTracks.value!![0].state = newState
			this.queueTracks.value = ArrayList(queueTracks.value!!)
		}
	}

	override fun onCompletion(mp: MediaPlayer?) {
		Log.i("TAGZ", "Song completed")
		reset()
	}

	fun reset() {
		mediaPlayer.reset()
		//currentTrackIndex.value = null
		queueTracks.value = null
		//currentTrack.value = null
	}

	fun isPlaying(): Boolean = mediaPlayer.isPlaying
}