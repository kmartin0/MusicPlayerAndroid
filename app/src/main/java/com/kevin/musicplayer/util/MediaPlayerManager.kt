package com.kevin.musicplayer.util

import android.media.MediaPlayer
import android.support.v4.media.session.PlaybackStateCompat
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

	fun toggle(): PlaybackStateCompat? {
		mediaPlayer?.let {
			return if (it.isPlaying) {
				pause()
				PlaybackStateHelper.STATE_PAUSED
			} else {
				play()
				PlaybackStateHelper.STATE_PLAYING
			}
		}
		return null
	}

	fun resume() {
		mediaPlayer?.start()
	}

	private fun createMediaPlayer() {
		reset()
		mediaPlayer = MediaPlayer()
		mediaPlayer!!.setOnCompletionListener(this)
	}

	override fun onCompletion(mp: MediaPlayer?) {
		Log.i("TAGZ", "Song completed")
		reset()
	}

	fun reset() {
		mediaPlayer?.release()
		mediaPlayer = null
	}
}