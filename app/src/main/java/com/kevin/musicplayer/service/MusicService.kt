package com.kevin.musicplayer.service

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
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
	private lateinit var mStateBuilder: PlaybackStateCompat.Builder
	private lateinit var notificationManager: NotificationManagerCompat
	private lateinit var notificationBuilder: NotificationBuilder

	override fun onCreate() {
		super.onCreate()
		Log.i(LOG_TAG, "onCreateService")
		initMediaSession()
		initNotification()
	}

	private fun initMediaSession() {
		// Create a MediaSessionCompat
		mediaSession = MediaSessionCompat(this, LOG_TAG).apply {

			// Enable callbacks from MediaButtons and TransportControls
			setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
					or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
					or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
			)

			// Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
			mStateBuilder = PlaybackStateCompat.Builder()
					.setActions(PlaybackStateCompat.ACTION_PLAY
							or PlaybackStateCompat.ACTION_PLAY_PAUSE
					)
			setPlaybackState(mStateBuilder.build())

			// MySessionCallback() has methods that handle callbacks from a media controller
			setCallback(mySessionCallback)

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
		if (MY_MEDIA_ROOT_ID == parentMediaId) {
			result.sendResult(MediaStoreDatabase(this).getAllTracks())
		} else {
			result.sendResult(null)
		}
	}

	private val mySessionCallback = object : MediaSessionCompat.Callback() {

		private val queue = ArrayList<MediaDescriptionCompat>()
		private var currentQueueIndex = 0

		override fun onPause() {
			Log.i(LOG_TAG, "onPause")
			MediaPlayerManager.getInstance().pause()
			mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PAUSED)
		}

		override fun onPlay() {
			Log.i(LOG_TAG, "onPlay")
			if (queue.isNotEmpty()) {
				val toPlay = queue[currentQueueIndex]
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

				startService(Intent(applicationContext, this@MusicService.javaClass))
				startForeground(NOW_PLAYING_NOTIFICATION, notification)
			}
		}

		override fun onSkipToNext() {
			if (queue.isNotEmpty()) {
				if (currentQueueIndex == queue.size - 1) {
					currentQueueIndex = 0
				} else {
					currentQueueIndex++
				}
			}
		}

		override fun onCustomAction(action: String?, extras: Bundle?) {
			if (action == "ACTION_TOGGLE") {
				val newState = MediaPlayerManager.getInstance().toggle()
				if (newState != null) mediaSession.setPlaybackState(newState)
			}
		}

		override fun onSkipToPrevious() {
			if (queue.isNotEmpty()) {
				if (currentQueueIndex == 0) {
					currentQueueIndex = queue.size - 1
				} else {
					currentQueueIndex--
				}
			}
		}

		override fun onSkipToQueueItem(id: Long) {
			currentQueueIndex = id.toInt()
		}

		override fun onStop() {
			MediaPlayerManager.getInstance().reset()
		}

		override fun onAddQueueItem(description: MediaDescriptionCompat?) {
			if (description != null) queue.add(description)
		}
	}
}

