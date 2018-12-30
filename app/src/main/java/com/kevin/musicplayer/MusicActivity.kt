package com.kevin.musicplayer

import android.content.ComponentName
import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.bumptech.glide.Glide
import com.kevin.musicplayer.base.BaseActivity
import com.kevin.musicplayer.service.MusicService
import kotlinx.android.synthetic.main.music_player_small.*

class MusicActivity : BaseActivity() {

	private lateinit var mMediaBrowser: MediaBrowserCompat
	private var mediaItem: MediaBrowserCompat.MediaItem? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mMediaBrowser = MediaBrowserCompat(this,
				ComponentName(this, MusicService::class.java),
				mConnectionCallbacks, null)
				.apply { connect() }

		mMediaBrowser.subscribe("media_root_id", subscriptionCallback)
	}

	override fun getLayoutId(): Int = R.layout.music_player_expand

	override fun getActivityTitle(): String = "Music"

	fun buildTransportControls() {
		val mediaController = MediaControllerCompat.getMediaController(this)
		// Grab the view for the play/pause button
		ivPlay.setOnClickListener {
			Log.i("TAGZ", "PLAY CLICKED")
//			val pbState = mediaController.playbackState.state
//			if (pbState == PlaybackStateCompat.STATE_PLAYING) {
//				mediaController.transportControls.pause()
//			} else {
//			MediaBrowser.MediaItem(MediaDescription.Builder().setMediaId("dur").build(), MediaBrowser.MediaItem.FLAG_PLAYABLE)
			val bundle = Bundle()

//			MediaBrowser.MediaItem(MediaDescription.Builder().setMediaId("dur").build(), MediaBrowser.MediaItem.FLAG_PLAYABLE)
//			MediaBrowserCompat.MediaItem(MediaDescriptionCompat.Builder().setMediaId("dur").build(), MediaBrowser.MediaItem.FLAG_PLAYABLE)

			bundle.putParcelable("MEDIA_ITEM", MediaBrowserCompat.MediaItem(MediaDescriptionCompat.Builder().setMediaId("dur").build(), MediaBrowser.MediaItem.FLAG_PLAYABLE))
//			bundle.putString("STRING_TEST", "STRINGY")

			mediaController.addQueueItem(mediaItem!!.description)
			mediaController.transportControls.play()
//			}
		}

		// Display the initial state
		val metadata = mediaController.metadata
		val pbState = mediaController.playbackState

		// Register a Callback to stay in sync
		mediaController.registerCallback(controllerCallback)
	}

	override fun onStop() {
		super.onStop()
		// (see "stay in sync with the MediaSession")
		MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback)
		mMediaBrowser.disconnect()
	}

	private var controllerCallback = object : MediaControllerCompat.Callback() {

		override fun onMetadataChanged(metadata: MediaMetadataCompat?) {}

		override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {}
	}

	private val mConnectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
		override fun onConnected() {
			Log.i("TAGZ", "onConnected")
			// Get the token for the MediaSession
			mMediaBrowser.sessionToken.also { token ->

				// Create a MediaControllerCompat
				val mediaController = MediaControllerCompat(
						this@MusicActivity, // Context
						token
				)

				// Save the controller
				MediaControllerCompat.setMediaController(this@MusicActivity, mediaController)
			}

			// Finish building the UI
			buildTransportControls()
		}

		override fun onConnectionSuspended() {
			// The Service has crashed. Disable transport controls until it automatically reconnects
			Log.i("TAGZ", "onConnectionSuspended")
		}

		override fun onConnectionFailed() {
			// The Service has refused our connection
			Log.i("TAGZ", "onConnectionFailed")
		}
	}

	private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
		override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
			super.onChildrenLoaded(parentId, children)
			Log.i("TAGZ", "Children: $children")
			if (children.isNotEmpty()) {
				val mediaItem = children[0]
				val albumArt = mediaItem.description.extras!!.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)
				Glide.with(this@MusicActivity).load(albumArt).into(ivAlbum)
				tvTrack.text = mediaItem.description.title
				tvArtist.text = mediaItem.description.subtitle
				this@MusicActivity.mediaItem = mediaItem
			}
		}

		override fun onError(parentId: String) {
			super.onError(parentId)
			Log.i("TAGZ", "onError")
		}
	}
}