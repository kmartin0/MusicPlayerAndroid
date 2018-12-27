package com.kevin.musicplayer.ui.player

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentMusicPlayerBinding
import com.kevin.musicplayer.model.MusicState
import com.kevin.musicplayer.model.Track
import com.kevin.musicplayer.service.MediaPlayerService
import kotlinx.android.synthetic.main.fragment_music_player.*
import kotlinx.android.synthetic.main.music_player_small.view.*

class MusicPlayerFragment : BaseMVVMFragment<FragmentMusicPlayerBinding, MusicPlayerViewModel>() {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initObservers()
	}

	private fun initObservers() {
		viewModel.mediaPlayerManager.queueTracks.observe(activity!!, Observer { queueTracks ->
			if (queueTracks != null && queueTracks.isNotEmpty()) {
				val currentQueueTrack = queueTracks[0]
				when (currentQueueTrack.state) {
					MusicState.PLAYING -> showPlayingState(currentQueueTrack.track)
					MusicState.PAUSING -> showPausingState(currentQueueTrack.track)
				}
			} else {
				showEmptyState()
			}
		})
		viewModel.resumeEvent.observe(activity!!, Observer { toggleIntent() })
		viewModel.pauseEvent.observe(activity!!, Observer { toggleIntent() })
	}

	private fun toggleIntent() {
		val i = Intent(context, MediaPlayerService::class.java)
		i.action = MediaPlayerService.ACTION_TOGGLE
		activity?.startService(i)
	}

	private fun showPlayingState(track: Track) {
		populatePlayerView(track)
		showPauseButton()
	}

	private fun showPausingState(track: Track) {
		populatePlayerView(track)
		showPlayButton()
	}

	private fun showEmptyState() {
		populatePlayerView(null)
		showPlayButton()
	}

	private fun populatePlayerView(track: Track?) {
		if (track != null) {
			if (track.album == null) setAlbumView(null) else setAlbumView(track.album!!.art)
			setTrackTitleView(track.title)
			setArtistView(track.artist?.name)
		} else {
			setAlbumView(null)
			setArtistView(null)
			setTrackTitleView("Empty Queue")
		}
	}

	private fun showPauseButton() {
		musicPlayerSmall.ivPause.visibility = View.VISIBLE
		musicPlayerExpand.ivPause.visibility = View.VISIBLE

		musicPlayerSmall.ivPlay.visibility = View.INVISIBLE
		musicPlayerExpand.ivPlay.visibility = View.INVISIBLE
	}

	private fun showPlayButton() {
		musicPlayerSmall.ivPause.visibility = View.INVISIBLE
		musicPlayerExpand.ivPause.visibility = View.INVISIBLE

		musicPlayerSmall.ivPlay.visibility = View.VISIBLE
		musicPlayerExpand.ivPlay.visibility = View.VISIBLE
	}

	private fun setAlbumView(artUri: String?) {
		if (artUri.isNullOrBlank()) {
			Glide.with(this).load(R.drawable.ic_album_placeholder).into(musicPlayerSmall.ivAlbum)
			Glide.with(this).load(R.drawable.ic_album_placeholder).into(musicPlayerExpand.ivAlbum)
		} else {
			Glide.with(this).load(artUri).into(musicPlayerSmall.ivAlbum)
			Glide.with(this).load(artUri).into(musicPlayerExpand.ivAlbum)
		}
	}

	private fun setArtistView(artist: String?) {
		if (artist == null) {
			musicPlayerSmall.tvArtist.text = ""
			musicPlayerExpand.tvArtist.text = ""
		} else {
			musicPlayerSmall.tvArtist.text = artist
			musicPlayerExpand.tvArtist.text = artist
		}
	}

	private fun setTrackTitleView(title: String?) {
		if (title == null) {
			musicPlayerSmall.tvTrack.text = ""
			musicPlayerExpand.tvTrack.text = ""
		} else {
			musicPlayerSmall.tvTrack.text = title
			musicPlayerExpand.tvTrack.text = title
		}
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<MusicPlayerViewModel> = MusicPlayerViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_music_player
}