package com.kevin.musicplayer.ui.player

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentMusicPlayerBinding
import com.kevin.musicplayer.service.MusicService
import kotlinx.android.synthetic.main.music_player_small.*

class MusicPlayerFragment : BaseMVVMFragment<FragmentMusicPlayerBinding, MusicPlayerViewModel>() {

	private lateinit var mMediaBrowser: MediaBrowserCompat


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		mMediaBrowser = MediaBrowserCompat(context,
				ComponentName(context!!, MusicService::class.java),
				mConnectionCallbacks, null)
				//.apply { connect() }

		mMediaBrowser.subscribe("media_root_id", subscriptionCallback)

	}

	override fun onStart() {
		super.onStart()
		mMediaBrowser.connect()
	}

	override fun onStop() {
		super.onStop()
		// (see "stay in sync with the MediaSession")
		if (activity != null) MediaControllerCompat.getMediaController(activity!!)?.unregisterCallback(controllerCallback)
		mMediaBrowser.disconnect()
	}

	fun buildTransportControls() {
		val mediaController = MediaControllerCompat.getMediaController(activity!!)
		// Grab the view for the play/pause button
		ivPlay.setOnClickListener {
			Log.i("TAGZ", "PLAY CLICKED")
			val pbState = mediaController.playbackState.state
			if (pbState == PlaybackStateCompat.STATE_PLAYING) {
				mediaController.transportControls.pause()
			} else {
				mediaController.transportControls.play()
			}
		}

		// Display the initial state
		val metadata = mediaController.metadata
		val pbState = mediaController.playbackState

		// Register a Callback to stay in sync
		mediaController.registerCallback(controllerCallback)
	}

	private val mConnectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
		override fun onConnected() {
			Log.i("TAGZ", "onConnected")
			// Get the token for the MediaSession
			mMediaBrowser.sessionToken.also { token ->

				// Create a MediaControllerCompat
				val mediaController = MediaControllerCompat(
						context, // Context
						token
				)

				// Save the controller
				MediaControllerCompat.setMediaController(activity!!, mediaController)
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

	private var controllerCallback = object : MediaControllerCompat.Callback() {

		override fun onMetadataChanged(metadata: MediaMetadataCompat?) {}

		override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {}
	}

	private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
		override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
			super.onChildrenLoaded(parentId, children)
			Log.i("TAGZ", "onChildrenLoaded")
		}

		override fun onError(parentId: String) {
			super.onError(parentId)
			Log.i("TAGZ", "onError")
		}
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<MusicPlayerViewModel> = MusicPlayerViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_music_player
}


//	private fun initObservers() {
//		viewModel.mediaPlayerManager.queueTracks.observe(activity!!, Observer { queueTracks ->
//			if (queueTracks != null && queueTracks.isNotEmpty()) {
//				val currentQueueTrack = queueTracks[0]
//				when (currentQueueTrack.state) {
//					MusicState.PLAYING -> showPlayingState(currentQueueTrack.track)
//					MusicState.PAUSING -> showPausingState(currentQueueTrack.track)
//				}
//			} else {
//				showEmptyState()
//			}
//		})
//		viewModel.resumeEvent.observe(activity!!, Observer { toggleIntent() })
//		viewModel.pauseEvent.observe(activity!!, Observer { toggleIntent() })
//	}
//
//	private fun toggleIntent() {
//		val i = Intent(context, MediaPlayerService::class.java)
//		i.action = MediaPlayerService.ACTION_TOGGLE
//		activity?.startService(i)
//	}
//
//	private fun showPlayingState(track: Track) {
//		populatePlayerView(track)
//		showPauseButton()
//	}
//
//	private fun showPausingState(track: Track) {
//		populatePlayerView(track)
//		showPlayButton()
//	}
//
//	private fun showEmptyState() {
//		populatePlayerView(null)
//		showPlayButton()
//	}
//
//	private fun populatePlayerView(track: Track?) {
//		if (track != null) {
//			if (track.album == null) setAlbumView(null) else setAlbumView(track.album!!.art)
//			setTrackTitleView(track.title)
//			setArtistView(track.artist?.name)
//		} else {
//			setAlbumView(null)
//			setArtistView(null)
//			setTrackTitleView("Empty Queue")
//		}
//	}
//
//	private fun showPauseButton() {
//		musicPlayerSmall.ivPause.visibility = View.VISIBLE
//		musicPlayerExpand.ivPause.visibility = View.VISIBLE
//
//		musicPlayerSmall.ivPlay.visibility = View.INVISIBLE
//		musicPlayerExpand.ivPlay.visibility = View.INVISIBLE
//	}
//
//	private fun showPlayButton() {
//		musicPlayerSmall.ivPause.visibility = View.INVISIBLE
//		musicPlayerExpand.ivPause.visibility = View.INVISIBLE
//
//		musicPlayerSmall.ivPlay.visibility = View.VISIBLE
//		musicPlayerExpand.ivPlay.visibility = View.VISIBLE
//	}
//
//	private fun setAlbumView(artUri: String?) {
//		if (artUri.isNullOrBlank()) {
//			Glide.with(this).load(R.drawable.ic_album_placeholder).into(musicPlayerSmall.ivAlbum)
//			Glide.with(this).load(R.drawable.ic_album_placeholder).into(musicPlayerExpand.ivAlbum)
//		} else {
//			Glide.with(this).load(artUri).into(musicPlayerSmall.ivAlbum)
//			Glide.with(this).load(artUri).into(musicPlayerExpand.ivAlbum)
//		}
//	}
//
//	private fun setArtistView(artist: String?) {
//		if (artist == null) {
//			musicPlayerSmall.tvArtist.text = ""
//			musicPlayerExpand.tvArtist.text = ""
//		} else {
//			musicPlayerSmall.tvArtist.text = artist
//			musicPlayerExpand.tvArtist.text = artist
//		}
//	}
//
//	private fun setTrackTitleView(title: String?) {
//		if (title == null) {
//			musicPlayerSmall.tvTrack.text = ""
//			musicPlayerExpand.tvTrack.text = ""
//		} else {
//			musicPlayerSmall.tvTrack.text = title
//			musicPlayerExpand.tvTrack.text = title
//		}
//	}
