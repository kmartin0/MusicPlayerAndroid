package com.kevin.musicplayer.ui.player

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentMusicPlayerBinding
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
		//viewModel.mediaPlayerManager.currentTrack.observe(activity!!, Observer { onTrackChanged(it) })
		viewModel.resumeEvent.observe(activity!!, Observer { toggleIntent() })
		viewModel.pauseEvent.observe(activity!!, Observer { toggleIntent() })
	}

	private fun onTrackChanged(track: Track?) {

		if (track != null) {
			if (activity != null) {
				if (track.album != null && !track.album!!.art.isNullOrBlank()) {
					Glide.with(this).load(track.album?.art).into(musicPlayerSmall.ivAlbum)
					Glide.with(this).load(track.album?.art).into(musicPlayerExpand.ivAlbum)
				} else {
					Glide.with(this).load(R.drawable.ic_album_placeholder).into(musicPlayerSmall.ivAlbum)
					Glide.with(this).load(R.drawable.ic_album_placeholder).into(musicPlayerExpand.ivAlbum)
				}
			}

			musicPlayerSmall.tvTrack.text = track.title
			musicPlayerExpand.tvTrack.text = track.title

			musicPlayerSmall.tvArtist.text = track.artist?.name
			musicPlayerExpand.tvArtist.text = track.artist?.name

			playingState()
		}
	}

	private fun toggleIntent() {
		val i = Intent(context, MediaPlayerService::class.java)
		i.action = MediaPlayerService.ACTION_TOGGLE
		activity?.startService(i)
	}

	private fun playingState() {
		Log.i("Servz", "Playing State")
		musicPlayerSmall.ivPause.visibility = View.VISIBLE
		musicPlayerExpand.ivPause.visibility = View.VISIBLE

		musicPlayerSmall.ivPlay.visibility = View.INVISIBLE
		musicPlayerExpand.ivPlay.visibility = View.INVISIBLE
	}

	private fun pausingState() {
		Log.i("Servz", "Pausing State")
		musicPlayerSmall.ivPause.visibility = View.INVISIBLE
		musicPlayerExpand.ivPause.visibility = View.INVISIBLE

		musicPlayerSmall.ivPlay.visibility = View.VISIBLE
		musicPlayerExpand.ivPlay.visibility = View.VISIBLE
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<MusicPlayerViewModel> = MusicPlayerViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_music_player
}