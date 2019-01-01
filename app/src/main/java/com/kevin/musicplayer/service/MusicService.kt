package com.kevin.musicplayer.service

import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.kevin.musicplayer.database.mediastore.MediaStoreDatabase
import com.kevin.musicplayer.util.MediaPlayerManager
import com.kevin.musicplayer.util.NOW_PLAYING_NOTIFICATION
import com.kevin.musicplayer.util.NotificationBuilder
import com.kevin.musicplayer.util.PlaybackStateHelper


private const val MY_MEDIA_ROOT_ID = "media_root_id"
const val LOG_TAG = "MusicServiceZ"

class MusicService : MediaBrowserServiceCompat() {

	private lateinit var mediaSession: MediaSessionCompat
	private lateinit var notificationManager: NotificationManagerCompat
	private lateinit var notificationBuilder: NotificationBuilder

	companion object {
		const val EXTRA_QUEUE_LIST = "EXTRA_QUEUE_LIST"
		const val ACTION_APPEND_QUEUE = "ACTION_APPEND_QUEUE"
	}

	override fun onCreate() {
		super.onCreate()
		Log.i(LOG_TAG, "onCreate")
		initMediaSession()
		initNotification()
	}

	private fun initMediaSession() {
		// Create a MediaSessionCompat
		mediaSession = MediaSessionCompat(this, LOG_TAG).apply {

			// Enable callbacks from MediaButtons and TransportControls
			setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
					or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
					or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
			)

			// Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
			setPlaybackState(PlaybackStateHelper.STATE_STOPPED)

			// MySessionCallback() has methods that handle callbacks from a media controller
			setCallback(MediaSessionCallback())

			// Set the session's token so that client activities can communicate with it.
			setSessionToken(sessionToken)
		}

	}

	private fun initNotification() {
		notificationBuilder = NotificationBuilder(this)
		notificationManager = NotificationManagerCompat.from(this)
	}

	override fun onGetRoot(
			clientPackageName: String,
			clientUid: Int,
			rootHints: Bundle?
	): MediaBrowserServiceCompat.BrowserRoot {
		Log.i(LOG_TAG, "onGetRoot")
		return MediaBrowserServiceCompat.BrowserRoot(MY_MEDIA_ROOT_ID, null)
	}

	override fun onLoadChildren(
			parentMediaId: String,
			result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>
	) {
		Log.i(LOG_TAG, "onLoadChildren")
		if (MY_MEDIA_ROOT_ID == parentMediaId) {
			result.sendResult(MediaStoreDatabase(this).getAllTracks())
		} else {
			result.sendResult(null)
		}
	}

	inner class MediaSessionCallback : MediaSessionCompat.Callback() {

		private val queue = ArrayList<MediaDescriptionCompat>()
		private var currentQueueIndex = 0

		override fun onPause() {
			Log.i(LOG_TAG, "onPause")
			MediaPlayerManager.getInstance().pause()
			mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PAUSED)
			stopForeground(false)
			notificationManager.notify(NOW_PLAYING_NOTIFICATION, notificationBuilder.buildNotification(mediaSession.sessionToken))
		}

		override fun onPlay() {
			Log.i(LOG_TAG, "onPlay")
			if (queue.isNotEmpty()) {
				Log.i("TAGZ", "MusicService.onPlay")
				val toPlay = queue[currentQueueIndex]
				if (mediaSession.controller.metadata?.description?.mediaId == toPlay.mediaId) {
					MediaPlayerManager.getInstance().resume()
					mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PLAYING)
					notificationManager.notify(NOW_PLAYING_NOTIFICATION, notificationBuilder.buildNotification(mediaSession.sessionToken))
				} else {
					MediaPlayerManager.getInstance().playMediaItem(toPlay.mediaUri.toString())

					val metaData = MediaMetadataCompat.Builder()
							.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, toPlay.mediaId)
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, toPlay.title.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, toPlay.subtitle.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, toPlay.description.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, toPlay.extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
							.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, toPlay.mediaUri.toString())
							.build()

					mediaSession.setMetadata(metaData)
					mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PLAYING)
					val notification = notificationBuilder.buildNotification(mediaSession.sessionToken)

					startForeground(NOW_PLAYING_NOTIFICATION, notification)
				}
			}
		}

		override fun onSkipToNext() {
			Log.i(LOG_TAG, "onSkipToNext")
			if (queue.isNotEmpty()) {
				if (currentQueueIndex == queue.size - 1) {
					currentQueueIndex = 0
				} else {
					currentQueueIndex++
				}
				onPlay()
			}
		}

		override fun onSkipToPrevious() {
			Log.i(LOG_TAG, "onSkipToPrevious")
			if (queue.isNotEmpty()) {
				if (currentQueueIndex == 0) {
					currentQueueIndex = queue.size - 1
				} else {
					currentQueueIndex--
				}
				onPlay()
			}
		}

		override fun onSkipToQueueItem(id: Long) {
			Log.i(LOG_TAG, "onSkipToQueueItem")
			currentQueueIndex = id.toInt()
			onPlay()
		}

		override fun onStop() {
			Log.i(LOG_TAG, "onStop")
			MediaPlayerManager.getInstance().reset()
			mediaSession.setPlaybackState(PlaybackStateHelper.STATE_STOPPED)
			queue.clear()
			currentQueueIndex = 0
			mediaSession.setMetadata(null)
		}

		override fun onAddQueueItem(description: MediaDescriptionCompat?) {
			Log.i(LOG_TAG, "onAddQueueItem")
			if (description != null) queue.add(description)
		}

		override fun onCustomAction(action: String?, extras: Bundle?) {
			Log.i("TAGZ", "onCustomAction")
			when (action) {
				ACTION_APPEND_QUEUE -> {
					extras?.classLoader = this@MusicService.classLoader
					queue.addAll(extras?.getParcelableArrayList<MediaDescriptionCompat>(EXTRA_QUEUE_LIST) as ArrayList<MediaDescriptionCompat>)
				}
			}
		}
	}
}

