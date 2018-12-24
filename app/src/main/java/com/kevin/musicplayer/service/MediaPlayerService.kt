package com.kevin.musicplayer.service

import android.app.*
import android.app.NotificationManager.IMPORTANCE_MIN
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.kevin.musicplayer.R
import com.kevin.musicplayer.model.Track
import com.kevin.musicplayer.util.MediaPlayerManager


class MediaPlayerService : Service() {

	companion object {
		const val ACTION_PLAY = "com.kevin.musicplayer.action.PLAY"
		const val ACTION_TOGGLE = "com.kevin.musicplayer.action.TOGGLE"
		const val ACTION_STOP = "com.kevin.musicplayer.action.STOP"
		const val EXTRA_TRACK = "com.kevin.musicplayer.extra.TRACK"
	}

	private val mediaPlayerManager = MediaPlayerManager.getInstance()

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onCreate() {
		super.onCreate()
		Log.i("Servz", "Creating Service")
		startForeground()
	}

	private fun startForeground() {
		val channelId =
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					createNotificationChannel("my_service", "My Background Service")
				} else {
					// If earlier version channel ID is not used
					// https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
					""
				}
		val stopIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_STOP }
		val pendingIntent = PendingIntent.getService(this, 0, stopIntent, 0)

		val notificationBuilder = NotificationCompat.Builder(this, channelId)
				.addAction(NotificationCompat.Action(R.drawable.ic_pause, "Stop Playing", pendingIntent))
		val notification = notificationBuilder.setOngoing(true)
				.setSmallIcon(R.drawable.ic_play)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build()
		startForeground(101, notification)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createNotificationChannel(channelId: String, channelName: String): String {
		val chan = NotificationChannel(channelId,
				channelName, NotificationManager.IMPORTANCE_NONE)
		chan.lightColor = Color.BLUE
		chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
		chan.importance = IMPORTANCE_MIN
		val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		service.createNotificationChannel(chan)
		return channelId
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.i("Servz", "Service On Start Command")
		when (intent?.action) {
			ACTION_PLAY -> processPlayRequest(intent.getParcelableExtra(EXTRA_TRACK))
			ACTION_TOGGLE -> processToggleRequest()
			ACTION_STOP -> processStopRequest()
		}
		return START_NOT_STICKY
	}

	private fun processPlayRequest(track: Track) {
		mediaPlayerManager.playTrack(track)
	}

	private fun processToggleRequest() {
		if (mediaPlayerManager.currentTrack.value != null) {
			if (mediaPlayerManager.isPlaying()) {
				mediaPlayerManager.pauseTrack()
			} else {
				mediaPlayerManager.resumeTrack()
			}
		}
	}

	private fun processStopRequest() {
		mediaPlayerManager.mediaPlayer.reset()
		stopSelf()
	}
}