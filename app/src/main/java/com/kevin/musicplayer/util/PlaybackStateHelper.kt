package com.kevin.musicplayer.util

import android.support.v4.media.session.PlaybackStateCompat
import com.kevin.musicplayer.R

class PlaybackStateHelper {
	companion object {
		val STATE_PLAYING = PlaybackStateCompat.Builder()
				.setActions(stateActions())
				.setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
				.addCustomAction("ACTION_RESUME", "ACTION_RESUME", R.drawable.ic_play)
				.build()!!

		val STATE_PAUSED = PlaybackStateCompat.Builder()
				.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
				.setActions(stateActions())
				.addCustomAction("ACTION_RESUME", "ACTION_RESUME", R.drawable.ic_play)
				.build()!!

		val STATE_STOPPED = PlaybackStateCompat.Builder()
				.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
				.setActions(stateActions())
				.addCustomAction("ACTION_RESUME", "ACTION_RESUME", R.drawable.ic_play)
				.build()!!

		private fun stateActions(): Long {
			return (PlaybackStateCompat.ACTION_PLAY
					or PlaybackStateCompat.ACTION_STOP
					or PlaybackStateCompat.ACTION_PAUSE
					or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
					or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
		}
	}

}