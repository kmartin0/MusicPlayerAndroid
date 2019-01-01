package com.kevin.musicplayer.ui.player

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import com.bumptech.glide.Glide
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseActivity
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentMusicPlayerBinding
import com.kevin.musicplayer.ui.lyrics.LyricsActivity
import com.kevin.musicplayer.ui.main.MainActivity
import com.kevin.musicplayer.util.BitmapHelper
import kotlinx.android.synthetic.main.fragment_music_player.*
import kotlinx.android.synthetic.main.music_player_expand.*
import kotlinx.android.synthetic.main.music_player_small.view.*


class MusicPlayerFragment : BaseMVVMFragment<FragmentMusicPlayerBinding, MusicPlayerViewModel>() {

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initObservers()
		ivLyrics.setOnClickListener { navigateToLyrics() }
	}

	private fun initObservers() {
		viewModel.currentTrack.observe(this, Observer { populateView(it) })
		viewModel.playBackState.observe(this, Observer { setControlButtons(it?.state) })
	}

	private fun populateView(metadata: MediaMetadataCompat?) {
		if (metadata == null) setEmptyState() else setTrackState(metadata)
	}

	private fun setEmptyState() {
		setAlbumIconView(null)
		setBackgroundView(null)
		setArtistView("")
		setTitleView("Empty Queue")
	}

	private fun setTrackState(metadata: MediaMetadataCompat) {
		val albumArtUri = metadata.bundle.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
		setBackgroundView(albumArtUri)
		setAlbumIconView(albumArtUri)
		setArtistView(metadata.description.description.toString())
		setTitleView(metadata.description.title.toString())
	}

	private fun setControlButtons(state: Int?) {
		when (state) {
			PlaybackStateCompat.STATE_PLAYING -> enablePauseButton()
			PlaybackStateCompat.STATE_PAUSED -> enablePlayButton()
			PlaybackStateCompat.STATE_STOPPED -> setEmptyState()
		}
	}

	private fun setArtistView(artist: String) {
		musicPlayerSmall.tvArtist.text = artist
		musicPlayerExpand.tvArtist.text = artist
	}

	private fun setTitleView(title: String) {
		musicPlayerSmall.tvTrack.text = title
		musicPlayerExpand.tvTrack.text = title
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

	private fun setAlbumIconView(albumArt: String?) {
		if (albumArt.isNullOrEmpty()) {
			Glide.with(context!!).load(R.drawable.ic_disc).into(musicPlayerSmall.ivAlbum)
			Glide.with(context!!).load(R.drawable.ic_disc).into(musicPlayerExpand.ivAlbum)
		} else {
			Glide.with(context!!).load(albumArt).into(musicPlayerSmall.ivAlbum)
			Glide.with(context!!).load(albumArt).into(musicPlayerExpand.ivAlbum)
		}
	}

	private fun setBackgroundView(albumArt: String?) {
		if (albumArt.isNullOrEmpty()) {
			ContextCompat.getColor(context!!, R.color.darkGrey).also {
				view?.setBackgroundColor(it)
				musicPlayerSmall.backGroundLine.setBackgroundColor(it)
				if (activity is MainActivity) {
					(activity as MainActivity).getRootView().setBackgroundColor(it)
				}
			}
		} else {
			BitmapHelper.blurBitmap(context!!, BitmapFactory.decodeFile(albumArt)).also {
				view?.background = BitmapDrawable(resources, it)
				musicPlayerSmall.backGroundLine.background = BitmapHelper.gradientFromBitmap(it)
				(activity as? MainActivity)?.getRootView()?.background = BitmapDrawable(resources, it)
			}
		}
	}

	private fun navigateToLyrics() {
		viewModel.currentTrack.value?.let {
			val intent = Intent(context!!, LyricsActivity::class.java)
			intent.putExtra("EXTRA_ARTIST", it.description.subtitle)
			intent.putExtra("EXTRA_TITLE", it.description.title)
			(activity!! as BaseActivity).startActivity(intent)
		}
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<MusicPlayerViewModel> = MusicPlayerViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_music_player
}
