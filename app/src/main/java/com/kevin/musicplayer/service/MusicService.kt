package com.kevin.musicplayer.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import com.intentfilter.androidpermissions.PermissionManager
import com.kevin.musicplayer.R
import com.kevin.musicplayer.repository.MediaStoreRepository
import com.kevin.musicplayer.util.*
import java.util.Collections.singleton


private const val LOG_TAG = "MusicServiceLogTag"

class MusicService : MediaBrowserServiceCompat() {

	private lateinit var mediaSession: MediaSessionCompat
	private lateinit var notificationManager: NotificationManagerCompat
	private lateinit var notificationBuilder: NotificationBuilder
	private lateinit var noisyReceiver: NoisyReceiver
	private lateinit var audioFocusRequest: AudioFocusRequest
	private val mediaStoreRepository = MediaStoreRepository(this)


	companion object {
		const val EXTRA_QUEUE_LIST = "EXTRA_QUEUE_LIST"
		const val ACTION_APPEND_QUEUE = "ACTION_APPEND_QUEUE"
		const val MY_MEDIA_ROOT_ID = "media_root_id"
	}

	override fun onCreate() {
		super.onCreate()
		initMediaSession()
		initNotification()
		initNoisyReceiver()
		initAudioFocusListener()
	}

	private fun initMediaSession() {
		// Create a MediaSessionCompat
		mediaSession = MediaSessionCompat(this, LOG_TAG).apply {

			// Enable callbacks from MediaButtons, TransportControls and QueueCommands
			setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
					or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
					or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
			)

			// Set an initial PlaybackState
			setPlaybackState(PlaybackStateHelper.STATE_STOPPED)

			// MySessionCallback() has methods that handle callbacks from a media controller
			setCallback(MediaSessionCallback())

			// Set the session's token so that client activities can communicate with it.
			setSessionToken(sessionToken)
		}
	}

	/**
	 * Initialize the notification builder and manager.
	 */
	private fun initNotification() {
		notificationBuilder = NotificationBuilder(this)
		notificationManager = NotificationManagerCompat.from(this)
	}

	private fun initNoisyReceiver() {
		noisyReceiver = NoisyReceiver(this, mediaSession.sessionToken)
	}

	private fun initAudioFocusListener() {
		audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
				.setAudioAttributes(AudioAttributes.Builder()
						.setUsage(AudioAttributes.USAGE_GAME)
						.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
						.build())
				.setOnAudioFocusChangeListener(FocusRequestListener(this@MusicService, mediaSession.sessionToken))
				.build()
	}

	/**
	 * @return the [MediaBrowserServiceCompat.BrowserRoot] of this app.
	 */
	override fun onGetRoot(
			clientPackageName: String,
			clientUid: Int,
			rootHints: Bundle?
	): MediaBrowserServiceCompat.BrowserRoot {
		return MediaBrowserServiceCompat.BrowserRoot(MY_MEDIA_ROOT_ID, null)
	}

	/**
	 * If the [parentMediaId] equals our [MY_MEDIA_ROOT_ID] all tracks on the device will be sent.
	 */
	override fun onLoadChildren(
			parentMediaId: String,
			result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>
	) {
		result.detach()
		val permissionManager = PermissionManager.getInstance(this)
		permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), object : PermissionManager.PermissionRequestListener {

			override fun onPermissionGranted() {
				result.sendResult(
						if (parentMediaId == MY_MEDIA_ROOT_ID) mediaStoreRepository.getAllTracks()
						else null)
			}

			override fun onPermissionDenied() {
				Toast.makeText(applicationContext, getString(R.string.store_permission_denied_msg), Toast.LENGTH_SHORT).show()
				result.sendResult(null)
			}
		})
	}

	/**
	 * Class containing callback methods for a [MediaSessionCompat]
	 */
	inner class MediaSessionCallback : MediaSessionCompat.Callback() {

		private val queue = ArrayList<MediaDescriptionCompat>()
		private var currentQueueIndex = 0
		private val mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

		/**
		 * Resumes if the current track if it is already playing. Otherwise it will start
		 * the current track from the beginning.
		 */
		override fun onPlay() {
			if (queue.isNotEmpty()) {
				if (mAudioManager.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					val toPlay = queue[currentQueueIndex]
					noisyReceiver.registerNoisyReceiver()
					if (mediaSession.controller.metadata?.description?.mediaId == toPlay.mediaId) { // Resume the current track
						MediaPlayerManager.getInstance().resume()
					} else { // Start the current track
						MediaPlayerManager.getInstance().playMediaItem(toPlay.mediaUri.toString())
						mediaSession.setMetadata(MediaHelper.descriptionCompatToMetadataCompat(toPlay))
					}
					mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PLAYING)
					startForeground(NOW_PLAYING_NOTIFICATION, notificationBuilder.buildNotification(mediaSession.sessionToken))
				}
			}
		}

		/**
		 * Pause the current track
		 */
		override fun onPause() {
			noisyReceiver.unregisterNoisyReceiver()
			MediaPlayerManager.getInstance().pause()
			mediaSession.setPlaybackState(PlaybackStateHelper.STATE_PAUSED)
			stopForeground(false)
			notificationManager.notify(NOW_PLAYING_NOTIFICATION, notificationBuilder.buildNotification(mediaSession.sessionToken))
		}

		/**
		 * Stop the MediaPlayer and clear the queue.
		 */
		override fun onStop() {
			mAudioManager.abandonAudioFocusRequest(audioFocusRequest)
			noisyReceiver.registerNoisyReceiver()
			MediaPlayerManager.getInstance().reset()
			mediaSession.setPlaybackState(PlaybackStateHelper.STATE_STOPPED)
			queue.clear()
			currentQueueIndex = 0
			mediaSession.setMetadata(null)
			stopForeground(true)
		}

		/**
		 * Plays the next track from the queue. If the current track is the last from the
		 * queue then the first track from the queue will be played.
		 */
		override fun onSkipToNext() {
			if (queue.isNotEmpty()) {
				if (currentQueueIndex == queue.size - 1) {
					currentQueueIndex = 0
				} else {
					currentQueueIndex++
				}
				onPlay()
			}
		}

		/**
		 * Plays the previous track from the queue. If the current track is the first from the
		 * queue then the last track from the queue will be played.
		 */
		override fun onSkipToPrevious() {
			if (queue.isNotEmpty()) {
				if (currentQueueIndex == 0) {
					currentQueueIndex = queue.size - 1
				} else {
					currentQueueIndex--
				}
				onPlay()
			}
		}

		/**
		 * If the [id] is in bounds of the queue then the track will be played.
		 */
		override fun onSkipToQueueItem(id: Long) {
			if (id >= 0 && id < queue.size) {
				currentQueueIndex = id.toInt()
				onPlay()
			}
		}

		/**
		 * Adds [description] at the end of the queue
		 */
		override fun onAddQueueItem(description: MediaDescriptionCompat?) {
			if (description != null) queue.add(description)
		}

		/**
		 * When the custom action equals [ACTION_APPEND_QUEUE] then List of [MediaDescriptionCompat]
		 * sent in the bundle with key [EXTRA_QUEUE_LIST] will be appended to the queue
		 */
		override fun onCustomAction(action: String?, extras: Bundle?) {
			when (action) {
				ACTION_APPEND_QUEUE -> {
					extras?.classLoader = this@MusicService.classLoader
					queue.addAll(extras?.getParcelableArrayList<MediaDescriptionCompat>(EXTRA_QUEUE_LIST) as ArrayList<MediaDescriptionCompat>)
				}
			}
		}
	}
}

/**
 * Helper class for listening for when headphones are unplugged (or the audio
 * will otherwise cause playback to become "noisy").
 */
private class NoisyReceiver(private val context: Context,
							sessionToken: MediaSessionCompat.Token)
	: BroadcastReceiver() {

	private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
	private val controller = MediaControllerCompat(context, sessionToken)

	private var registered = false

	fun registerNoisyReceiver() {
		if (!registered) {
			context.registerReceiver(this, noisyIntentFilter)
			registered = true
		}
	}

	fun unregisterNoisyReceiver() {
		if (registered) {
			context.unregisterReceiver(this)
			registered = false
		}
	}

	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
			controller.transportControls.pause()
		}
	}
}

private class FocusRequestListener(context: Context, sessionToken: MediaSessionCompat.Token)
	: AudioManager.OnAudioFocusChangeListener {

	private val controller = MediaControllerCompat(context, sessionToken)

	override fun onAudioFocusChange(newFocus: Int) {
		when (newFocus) {
			AudioManager.AUDIOFOCUS_GAIN -> controller.transportControls.play()
			AudioManager.AUDIOFOCUS_LOSS,
			AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
			AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> controller.transportControls.pause()
		}
	}
}
