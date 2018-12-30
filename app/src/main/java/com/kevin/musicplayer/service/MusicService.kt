package com.kevin.musicplayer.service

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.MediaPlayer
import android.net.Uri
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
import com.kevin.musicplayer.util.NOW_PLAYING_NOTIFICATION
import com.kevin.musicplayer.util.NotificationBuilder


private const val MY_MEDIA_ROOT_ID = "media_root_id"
const val LOG_TAG = "TAGZ"

class MusicService : MediaBrowserServiceCompat() {

	private var mMediaSession: MediaSessionCompat? = null
	private var mediaPlayer: MediaPlayer? = null
	private lateinit var mStateBuilder: PlaybackStateCompat.Builder
	private lateinit var notificationManager: NotificationManagerCompat
	private lateinit var notificationBuilder: NotificationBuilder

	override fun onCreate() {
		super.onCreate()
		Log.i(LOG_TAG, "onCreateService")
		initMediaPlayer()
		initMediaSession()
		initNotification()
	}


	private fun initMediaPlayer() {
		mediaPlayer = MediaPlayer()
		//	mediaPlayer!!.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
		mediaPlayer!!.setAudioAttributes(AudioAttributes.Builder().setFlags(CONTENT_TYPE_MUSIC).build())
	}

	private fun initMediaSession() {
		// Create a MediaSessionCompat
		mMediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {

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

		override fun onPause() {
			super.onPause()
			Log.i(LOG_TAG, "onPause")
		}

		override fun onPlay() {
			super.onPlay()
			Log.i(LOG_TAG, "onPlay")
			if (mMediaSession != null) {
				if (queue.isNotEmpty()) {

					val toPlay = queue[0]

					mediaPlayer!!.reset()
					mediaPlayer!!.setDataSource(this@MusicService, toPlay.mediaUri!!)
					mediaPlayer!!.prepare()
					mediaPlayer!!.start()

					val metaData = MediaMetadataCompat.Builder()
							.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, toPlay.mediaId)
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, toPlay.title.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, toPlay.subtitle.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, toPlay.description.toString())
							.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, toPlay.extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
							.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, toPlay.mediaUri.toString())
							.build()

					mMediaSession!!.setMetadata(metaData)
					val notification = notificationBuilder.buildNotification(mMediaSession!!.sessionToken)

					startService(Intent(applicationContext, this@MusicService.javaClass))
					startForeground(NOW_PLAYING_NOTIFICATION, notification)
				}
			}
		}

		override fun onAddQueueItem(description: MediaDescriptionCompat?) {
			super.onAddQueueItem(description)
			if (description != null) queue.add(description)
		}
	}
}