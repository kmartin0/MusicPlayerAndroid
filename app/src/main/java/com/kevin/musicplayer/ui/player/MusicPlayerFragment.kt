package com.kevin.musicplayer.ui.player

import android.arch.lifecycle.Observer
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import com.bumptech.glide.Glide
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentMusicPlayerBinding
import com.kevin.musicplayer.ui.main.MainActivity
import com.kevin.musicplayer.util.BitmapHelper
import kotlinx.android.synthetic.main.fragment_music_player.*
import kotlinx.android.synthetic.main.music_player_small.view.*


class MusicPlayerFragment : BaseMVVMFragment<FragmentMusicPlayerBinding, MusicPlayerViewModel>() {

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initObservers()
	}

	private fun initObservers() {
		viewModel.currentTrack.observe(this, Observer { populateView(it) })
		viewModel.playBackState.observe(this, Observer { setControlButtons(it?.state) })
	}

	private fun populateView(metadata: MediaMetadataCompat?) {
		if (metadata == null) setEmptyState() else setTrackState(metadata)
	}

	private fun setEmptyState() {
		Glide.with(context!!).load(R.drawable.ic_disc).into(musicPlayerSmall.ivAlbum)
		Glide.with(context!!).load(R.drawable.ic_disc).into(musicPlayerExpand.ivAlbum)

		musicPlayerSmall.tvTrack.text = "Empty Queue"
		musicPlayerExpand.tvTrack.text = "Empty Queue"

		musicPlayerSmall.tvArtist.text = ""
		musicPlayerExpand.tvArtist.text = ""
	}

	private fun setTrackState(metadata: MediaMetadataCompat) {
		val albumArt = metadata.bundle.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)

		if (albumArt.isNullOrEmpty()) {
			Glide.with(context!!).load(R.drawable.ic_disc).into(musicPlayerSmall.ivAlbum)
			Glide.with(context!!).load(R.drawable.ic_disc).into(musicPlayerExpand.ivAlbum)

			view?.setBackgroundColor(ContextCompat.getColor(context!!, R.color.darkGrey))
			if (activity is MainActivity) {
				(activity as MainActivity).getRootView().setBackgroundColor(ContextCompat.getColor(context!!, R.color.darkGrey))
			}
		} else {
			Glide.with(context!!).load(albumArt).into(musicPlayerSmall.ivAlbum)
			Glide.with(context!!).load(albumArt).into(musicPlayerExpand.ivAlbum)

			val blurredAlbumArt = BitmapHelper.blurBitmap(context!!, BitmapFactory.decodeFile(albumArt))
			view?.background = BitmapDrawable(resources, blurredAlbumArt)
			if (activity is MainActivity) {
				(activity as MainActivity).getRootView().background = BitmapDrawable(resources, blurredAlbumArt)
			}
		}

		musicPlayerSmall.tvTrack.text = metadata.description.title
		musicPlayerExpand.tvTrack.text = metadata.description.title

		musicPlayerSmall.tvArtist.text = metadata.description.description
		musicPlayerExpand.tvArtist.text = metadata.description.description
	}

	private fun setControlButtons(state: Int?) {
		when (state) {
			PlaybackStateCompat.STATE_PLAYING -> enablePauseButton()
			PlaybackStateCompat.STATE_PAUSED -> enablePlayButton()
		}
	}

	private fun enablePlayButton() {
		musicPlayerSmall.ivPlay.visibility = View.VISIBLE
		musicPlayerExpand.ivPlay.visibility = View.VISIBLE

		musicPlayerSmall.ivPause.visibility = View.INVISIBLE
		musicPlayerExpand.ivPause.visibility = View.INVISIBLE
	}

	private fun enablePauseButton() {
		musicPlayerSmall.ivPause.visibility = View.VISIBLE
		musicPlayerExpand.ivPause.visibility = View.VISIBLE

		musicPlayerSmall.ivPlay.visibility = View.INVISIBLE
		musicPlayerExpand.ivPlay.visibility = View.INVISIBLE
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<MusicPlayerViewModel> = MusicPlayerViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_music_player
}
