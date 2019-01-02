package com.kevin.musicplayer.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.kevin.musicplayer.R
import com.kevin.musicplayer.ui.main.MainActivity

const val NOW_PLAYING_CHANNEL: String = "com.kevin.musicplayer.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION: Int = 0xb339
const val REQUEST_CODE: Int = 501

/**
 * Helper class for building notifications.
 */
class NotificationBuilder(private val context: Context) {
	private val platformNotificationManager: NotificationManager =
			context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

	// Pending Intent which calls [PlaybackStateCompat.ACTION_STOP]
	private val stopPendingIntent =
			MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

	// Action which calls [PlaybackStateCompat.ACTION_PAUSE]
	private val pauseAction = NotificationCompat.Action(
			android.R.drawable.ic_media_pause,
			"Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(
			context, PlaybackStateCompat.ACTION_PAUSE))

	// Action which calls [PlaybackStateCompat.ACTION_PLAY]
	private val playAction = NotificationCompat.Action(
			android.R.drawable.ic_media_play,
			"Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(
			context, PlaybackStateCompat.ACTION_PLAY))

	// Action which calls [PlaybackStateCompat.ACTION_SKIP_TO_NEXT]
	private val skipToNextAction = NotificationCompat.Action(
			android.R.drawable.ic_media_next,
			"Next", MediaButtonReceiver.buildMediaButtonPendingIntent(
			context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

	// Action which calls [PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS]
	private val skipToPreviousAction = NotificationCompat.Action(
			android.R.drawable.ic_media_previous,
			"Next", MediaButtonReceiver.buildMediaButtonPendingIntent(
			context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))

	/**
	 * Builds a media style notification for a [sessionToken]
	 */
	fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
		if (shouldCreateNowPlayingChannel()) {
			createNowPlayingChannel()
		}

		val controller = MediaControllerCompat(context, sessionToken)
		val description = controller.metadata.description
		val playbackState = controller.playbackState
		val albumUri = controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)

		val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

		val mediaStyle = MediaStyle()
				.setCancelButtonIntent(stopPendingIntent)
				.setMediaSession(sessionToken)
				.setShowActionsInCompactView(0, 1, 2)
				.setShowCancelButton(true)

		builder.addAction(skipToPreviousAction)
		when (playbackState.state) {
			PlaybackStateCompat.STATE_PAUSED -> builder.addAction(playAction)
			PlaybackStateCompat.STATE_PLAYING -> builder.addAction(pauseAction)
		}
		builder.addAction(skipToNextAction)

		return builder.setContentIntent(createContentIntent())
				.setContentTitle(description.title)
				.setContentText(description.description)
				.setDeleteIntent(stopPendingIntent)
				.setOnlyAlertOnce(true)
				.setSmallIcon(R.drawable.ic_play)
				.setLargeIcon(getLargeIcon(albumUri))
				.setStyle(mediaStyle)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.build()
	}

	private fun getLargeIcon(albumUri: String?): Bitmap {
		return if (albumUri != null)
			BitmapFactory.decodeFile(albumUri)
		else BitmapHelper.drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_disc)!!)
	}

	private fun createContentIntent(): PendingIntent {
		val openUI = Intent(context, MainActivity::class.java)
		openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
		return PendingIntent.getActivity(
				context, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT)
	}

	private fun shouldCreateNowPlayingChannel() =
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

	@RequiresApi(Build.VERSION_CODES.O)
	private fun nowPlayingChannelExists() =
			platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createNowPlayingChannel() {
		val notificationChannel = NotificationChannel(NOW_PLAYING_CHANNEL,
				context.getString(R.string.notification_channel),
				NotificationManager.IMPORTANCE_LOW)
				.apply {
					description = context.getString(R.string.notification_channel_description)
				}

		platformNotificationManager.createNotificationChannel(notificationChannel)
	}
}