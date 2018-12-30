package com.kevin.musicplayer.util

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.kevin.musicplayer.service.MusicService

const val LOG_TAG = "MediaSessionConnectionZ"

class MediaSessionConnection(context: Context) {

	private val serviceComponent = ComponentName(context, MusicService::class.java)
	lateinit var mediaController: MediaControllerCompat
	val transportControls: MediaControllerCompat.TransportControls
		get() = mediaController.transportControls

	val isConnected = MutableLiveData<Boolean>().apply { postValue(false) }
	val playBackState = MutableLiveData<PlaybackStateCompat>().apply { postValue(EMPTY_PLAYBACK_STATE) }
	val currentTrack = MutableLiveData<MediaMetadataCompat>().apply { postValue(NOTHING_PLAYING) }
	val mediaItems = MutableLiveData<List<MediaBrowserCompat.MediaItem>>()

	private val mediaBrowser = MediaBrowserCompat(context,
			serviceComponent,
			MediaBrowserConnectionCallback(context), null)
			.apply {
				connect()
				subscribe("media_root_id", SubscriptionCallback())
			}

	inner class MediaBrowserConnectionCallback(val context: Context) : MediaBrowserCompat.ConnectionCallback() {
		override fun onConnected() {
			Log.i(LOG_TAG, "onConnected")
			mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
				registerCallback(MediaControllerCallback())

			}
		}

		override fun onConnectionSuspended() {
			// The Service has crashed. Disable transport controls until it automatically reconnects
			Log.i(LOG_TAG, "onConnectionSuspended")
		}

		override fun onConnectionFailed() {
			// The Service has refused our connection
			Log.i(LOG_TAG, "onConnectionFailed")
		}

	}

	inner class SubscriptionCallback : MediaBrowserCompat.SubscriptionCallback() {
		override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
			super.onChildrenLoaded(parentId, children)
			Log.i(LOG_TAG, "onChildrenLoaded: $children")
			mediaItems.postValue(children)
		}

		override fun onError(parentId: String) {
			super.onError(parentId)
			Log.i(LOG_TAG, "onError")
		}
	}

	inner class MediaControllerCallback : MediaControllerCompat.Callback() {

		override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
			Log.i(LOG_TAG, "onPlaybackStateChanged $state")
			playBackState.postValue(state)
		}

		override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
			Log.i(LOG_TAG, "onMetadataChanged $metadata")
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

	@Suppress("PropertyName")
	val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
			.setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
			.build()

	@Suppress("PropertyName")
	val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
			.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
			.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
			.build()
}

