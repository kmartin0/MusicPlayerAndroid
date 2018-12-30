package com.kevin.musicplayer.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.kevin.musicplayer.R

const val NOW_PLAYING_CHANNEL: String = "com.kevin.musicplayer.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION: Int = 0xb339

/**
 * Helper class to encapsulate code for building notifications.
 */
class NotificationBuilder(private val context: Context) {
	private val platformNotificationManager: NotificationManager =
			context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

	private val stopPendingIntent =
			MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

	fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
		if (shouldCreateNowPlayingChannel()) {
			createNowPlayingChannel()
		}

		val controller = MediaControllerCompat(context, sessionToken)
		val description = controller.metadata.description
		val playbackState = controller.playbackState

		val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)


		val mediaStyle = android.support.v4.media.app.NotificationCompat.MediaStyle()
				.setCancelButtonIntent(stopPendingIntent)
				.setMediaSession(sessionToken)
				.setShowActionsInCompactView(0)
				.setShowCancelButton(true)

		return builder.setContentIntent(controller.sessionActivity)
				.setContentText(description.subtitle)
				.setContentTitle(description.title)
				.setDeleteIntent(stopPendingIntent)
				.setLargeIcon(description.iconBitmap)
				.setOnlyAlertOnce(true)
				.setSmallIcon(R.drawable.ic_play)
				.setStyle(mediaStyle)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.addAction(NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE)))
				.build()
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