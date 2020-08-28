package com.kevin.musicplayer.util

import androidx.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.kevin.musicplayer.service.MusicService

/**
 * Singleton class to manage a [MediaSessionCompat]
 * Contains the following callback classes:
 * 	- [MediaBrowserCompat.ConnectionCallback]
 * 	- [MediaBrowserCompat.SubscriptionCallback]
 * 	- [MediaControllerCompat.Callback]
 */
class MediaSessionConnection(context: Context) {

	private val serviceComponent = ComponentName(context, MusicService::class.java)
	lateinit var mediaController: MediaControllerCompat
	val transportControls: MediaControllerCompat.TransportControls
		get() = mediaController.transportControls

	val playBackState = MutableLiveData<PlaybackStateCompat>()
	val currentTrack = MutableLiveData<MediaMetadataCompat>()
	val mediaItems = MutableLiveData<List<MediaBrowserCompat.MediaItem>>()

	/**
	 * Makes a connection with the [MediaBrowserServiceCompat] from the [MusicService]
	 *  and connects and subscribes to its [MediaBrowserCompat]
	 */
	private val mediaBrowser = MediaBrowserCompat(context,
			serviceComponent,
			MediaBrowserConnectionCallback(context), null)
			.apply {
				connect()
				subscribe(MusicService.MY_MEDIA_ROOT_ID, SubscriptionCallback())
			}

	/**
	 * Callback class to manage the [MediaBrowserCompat.ConnectionCallback]
	 */
	inner class MediaBrowserConnectionCallback(val context: Context) : MediaBrowserCompat.ConnectionCallback() {
		/**
		 * Registers the [MediaControllerCallback] to listen for media control changes.
		 */
		override fun onConnected() {
			mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
				registerCallback(MediaControllerCallback())
			}
		}
	}

	/**
	 * Callback class to manage the [MediaBrowserCompat.SubscriptionCallback]
	 */
	inner class SubscriptionCallback : MediaBrowserCompat.SubscriptionCallback() {
		/**
		 * When the children are the loaded post the value of [children] to [mediaItems]
		 */
		override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
			super.onChildrenLoaded(parentId, children)
			mediaItems.postValue(children)
		}
	}

	/**
	 * Callback class to manage the [MediaControllerCompat.Callback]
	 */
	inner class MediaControllerCallback : MediaControllerCompat.Callback() {
		/**
		 * When the play back state is changed, post the value of [state] to [playBackState]
		 */
		override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
			playBackState.postValue(state)
		}

		/**
		 * When the metadata is changed, post the value of [metadata] to [currentTrack]
		 */
		override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
			currentTrack.postValue(metadata)
		}
	}

	companion object {
		// For Singleton instantiation.
		@Volatile
		private var instance: MediaSessionConnection? = null

		fun getInstance(context: Context): MediaSessionConnection =
				instance ?: synchronized(this) {
					instance ?: MediaSessionConnection(context)
							.also { instance = it }
				}
	}
}

