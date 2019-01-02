package com.kevin.musicplayer.ui.tracklist

import android.app.Application
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.service.MusicService
import com.kevin.musicplayer.util.MediaSessionConnection

class TrackListViewModel(application: Application) : BaseViewModel(application) {

	private val mediaSessionConnection = MediaSessionConnection.getInstance(application.applicationContext)
	val trackList = mediaSessionConnection.mediaItems
	val currentTrack = mediaSessionConnection.currentTrack

	/**
	 * Load the [trackList] in the queue of the music player and play the [mediaItem]
	 */
	fun play(mediaItem: MediaBrowserCompat.MediaItem) {
		mediaSessionConnection.transportControls.stop()

		// Transform the track list to a list of MediaDescriptionCompat.
		val mediaDescriptionList = ArrayList<MediaDescriptionCompat>()
		trackList.value?.mapTo(mediaDescriptionList) { it.description }

		// Send the queue to the media session in chunks of 300 to prevent TransactionTooLargeException.
		mediaDescriptionList.chunked(300).forEach {
			val bundle = Bundle()
			bundle.putParcelableArrayList(MusicService.EXTRA_QUEUE_LIST, it as ArrayList<MediaDescriptionCompat>)
			mediaSessionConnection.transportControls.sendCustomAction(MusicService.ACTION_APPEND_QUEUE, bundle)
		}

		// Set the current queue position to the selected track.
		val queueIndex = trackList.value!!.indexOf(mediaItem).toLong()
		mediaSessionConnection.transportControls.skipToQueueItem(queueIndex)
	}

}
