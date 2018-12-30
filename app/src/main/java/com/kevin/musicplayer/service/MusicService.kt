package com.kevin.musicplayer.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.kevin.musicplayer.R
import com.kevin.musicplayer.database.mediastore.MediaStoreDatabase
import com.kevin.musicplayer.util.*


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
		Log.i(LOG_TAG, "onLoadChildren")
		val testTrack = MediaStoreDatabase(this).getLittleWing()!!
		// Assume for example that the music catalog is already loaded/cached.
		val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()

		// Check if this is the root menu:
		if (MY_MEDIA_ROOT_ID == parentMediaId) {
			// Build the MediaItem objects for the top level,
			// and put them in the mediaItems list...
			val mediaId = testTrack.id
			//Artist song
			val title = testTrack.title
			//Artist name
			val subTitle = testTrack.artist!!.name
			//Artist album
			val descriptin = testTrack.album!!.title
			//Song duration
			val duration: Long = 200
			// Uri
			val url = Uri.parse(testTrack.data)

			val extras = Bundle()
			extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
			val albumArt = testTrack.album!!.art
			extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)

			val desc = MediaDescriptionCompat.Builder()
					.setMediaId(mediaId)
					.setTitle(title)
					.setSubtitle(subTitle)
					.setDescription(descriptin)
					.setExtras(extras)
					.setMediaUri(url)
					.build()

			val songList = MediaBrowserCompat.MediaItem(desc,
					MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
			mediaItems.add(songList)
			Log.i(LOG_TAG, "Added a song")
		} else {
			// Examine the passed parentMediaId to see which submenu we're at,
			// and put the children of that menu in the mediaItems list...
		}
		result.sendResult(mediaItems)
	}

	private val mySessionCallback = object : MediaSessionCompat.Callback() {

		private val queue = ArrayList<MediaDescriptionCompat>()
		private var isPaused = false

		override fun onPause() {
			super.onPause()
			Log.i(LOG_TAG, "onPause")
			MediaPlayerManager.getInstance().pause()
			mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PAUSED)
			isPaused = true
		}

		override fun onPlay() {
			super.onPlay()
			Log.i(LOG_TAG, "onPlay")
			if (queue.isNotEmpty()) {
				if (isPaused) {
					MediaPlayerManager.getInstance().play()
					mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PLAYING)
				} else {
					val toPlay = queue[0]
					MediaPlayerManager.getInstance().playMediaItem(toPlay.mediaUri.toString())

					val metaData = MediaMetadataCompat.Builder()
							.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, toPlay.mediaId)
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, toPlay.title.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, toPlay.subtitle.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, toPlay.description.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, toPlay.extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
							.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, toPlay.mediaUri.toString())
							.build()

					mediaSession.setMetadata(metaData)
					mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PLAYING)
					val notification = notificationBuilder.buildNotification(mediaSession.sessionToken)

					startService(Intent(applicationContext, this@MusicService.javaClass))
					startForeground(NOW_PLAYING_NOTIFICATION, notification)
				}
				isPaused = false
			}
		}

		override fun onAddQueueItem(description: MediaDescriptionCompat?) {
			super.onAddQueueItem(description)
			if (description != null) queue.add(description)
		}
	}
}