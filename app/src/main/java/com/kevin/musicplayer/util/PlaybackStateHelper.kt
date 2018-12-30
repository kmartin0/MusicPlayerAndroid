package com.kevin.musicplayer.util

import android.support.v4.media.session.PlaybackStateCompat

class PlaybackStateHelper {
	companion object {
		val STATE_PLAYING = PlaybackStateCompat.Builder()
				.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
				.build()!!

		val STATE_PAUSED = PlaybackStateCompat.Builder()
				.setState(PlaybackStateCompat.STATE_PAUSED, 0, 1f)
				.build()!!
	}
}