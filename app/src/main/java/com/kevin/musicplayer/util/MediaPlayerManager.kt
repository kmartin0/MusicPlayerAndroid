package com.kevin.musicplayer.util

import android.media.MediaPlayer
import android.util.Log

// TODO: Handle incoming calls, headphone remove, different audio focus.
class MediaPlayerManager : MediaPlayer.OnCompletionListener {
	private var mediaPlayer: MediaPlayer? = null

	companion object {
		// For Singleton instantiation.
		@Volatile
		private var instance: MediaPlayerManager? = null

		fun getInstance(): MediaPlayerManager =
				instance ?: synchronized(this) {
					instance ?: MediaPlayerManager()
							.also { instance = it }
				}
	}

	fun playMediaItem(trackUri: String) {
		createMediaPlayer()
		mediaPlayer!!.setDataSource(trackUri)
		mediaPlayer!!.prepare()
		mediaPlayer!!.start()
	}

	fun play() {
		mediaPlayer?.start()
	}

	fun pause() {
		mediaPlayer?.pause()
	}


	private fun createMediaPlayer() {
		mediaPlayer?.release()
		mediaPlayer = MediaPlayer()
		mediaPlayer!!.setOnCompletionListener(this)
	}

	override fun onCompletion(mp: MediaPlayer?) {
		Log.i("TAGZ", "Song completed")
		mediaPlayer?.release()
		mediaPlayer = null
	}
}