package com.kevin.musicplayer.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

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
	fun playMediaItem(trackUri: Uri, context: Context, onCompletionCallback: () -> Unit) {
		createMediaPlayer(onCompletionCallback)
		mediaPlayer!!.setDataSource(context, trackUri)
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
	private fun createMediaPlayer(onCompletionCallback: () -> Unit) {
		reset()
		mediaPlayer = MediaPlayer()
		mediaPlayer!!.setOnCompletionListener {
			reset()
			onCompletionCallback()
		}
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