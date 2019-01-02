package com.kevin.musicplayer.util

import android.media.MediaPlayer

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

	/**
	 * Create a new [MediaPlayer] and play the [trackUri]
	 */
	fun playMediaItem(trackUri: String) {
		createMediaPlayer()
		mediaPlayer!!.setDataSource(trackUri)
		mediaPlayer!!.prepare()
		mediaPlayer!!.start()
	}

	/**
	 * Pause the [mediaPlayer]
	 */
	fun pause() {
		mediaPlayer?.pause()
	}

	/**
	 * Start the [mediaPlayer]
	 */
	fun resume() {
		mediaPlayer?.start()
	}

	/**
	 * Release the [mediaPlayer] and create a new [MediaPlayer] object.
	 */
	private fun createMediaPlayer() {
		reset()
		mediaPlayer = MediaPlayer()
		mediaPlayer!!.setOnCompletionListener(this)
	}

	/**
	 * Release the [mediaPlayer] when the current track is completed.
	 */
	override fun onCompletion(mp: MediaPlayer?) {
		reset()
	}

	/**
	 * Release the [mediaPlayer] and set its value to null
	 */
	fun reset() {
		mediaPlayer?.release()
		mediaPlayer = null
	}
}