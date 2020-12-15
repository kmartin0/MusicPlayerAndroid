package com.kevin.musicplayer.ui.player

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseActivity
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentMusicPlayerBinding
import com.kevin.musicplayer.ui.lyrics.LyricsActivity
import com.kevin.musicplayer.ui.main.MainActivity
import com.kevin.musicplayer.util.AlbumArtHelper
import com.kevin.musicplayer.util.BitmapHelper

@Suppress("unused")
class MusicPlayerFragment : BaseMVVMFragment<FragmentMusicPlayerBinding, MusicPlayerViewModel>() {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.musicPlayerExpand.root.alpha = 0f
		initObservers()
	}

	/**
	 * Observe [MusicPlayerViewModel.currentTrack] and set the view with it's metadata
	 * Observe [MusicPlayerViewModel.playBackState] and set the play/pause button
	 * Observe [MusicPlayerViewModel.navigateLyricsEvent] and start [LyricsActivity] if the event is called
	 */
	private fun initObservers() {
		viewModel.currentTrack.observe(viewLifecycleOwner, { populateView(it) })
		viewModel.playBackState.observe(viewLifecycleOwner, { setControlButtons(it?.state) })
		viewModel.navigateLyricsEvent.observe(viewLifecycleOwner, { navigateToLyrics() })
	}

	/**
	 * Set the empty state when [metadata] is null otherwise fill the view with the [metadata]
	 */
	private fun populateView(metadata: MediaMetadataCompat?) {
		if (metadata == null) setEmptyState() else setTrackState(metadata)
	}

	/**
	 * Displays the album placeholder, grey background and the empty queue text
	 */
	private fun setEmptyState() {
		setAlbumIconView(null)
		setBackgroundView(null)
		setArtistView("")
		setTitleView(getString(R.string.empty_queue))
	}

	/**
	 * Sets the album icon, background, artist and title for the [metadata].
	 */
	private fun setTrackState(metadata: MediaMetadataCompat) {
		with(metadata) {
			setBackgroundView(description.mediaUri)
			setAlbumIconView(description.mediaUri)
			setArtistView(description.description.toString())
			setTitleView(description.title.toString())
		}
	}

	/**
	 * Display the pause button when the music player is playing
	 * Display the play button when the music player is pausing or stopped
	 */
	private fun setControlButtons(state: Int?) {
		when (state) {
			PlaybackStateCompat.STATE_PLAYING -> enablePauseButton()
			PlaybackStateCompat.STATE_PAUSED -> enablePlayButton()
			PlaybackStateCompat.STATE_STOPPED -> setEmptyState()
		}
	}

	/**
	 * Sets the artist for the small and large music player.
	 */
	private fun setArtistView(artist: String) {
		binding.musicPlayerSmall.tvArtist.text = artist
		binding.musicPlayerExpand.tvArtist.text = artist
	}

	/**
	 * Sets the track title for the small and large music player.
	 */
	private fun setTitleView(title: String) {
		binding.musicPlayerSmall.tvTrack.text = title
		binding.musicPlayerExpand.tvTrack.text = title
	}

	/**
	 * Enabled the play button and disables the pause button for the small and large music player.
	 */
	private fun enablePlayButton() {
		binding.musicPlayerSmall.ivPlay.visibility = View.VISIBLE
		binding.musicPlayerExpand.ivPlay.visibility = View.VISIBLE

		binding.musicPlayerSmall.ivPause.visibility = View.INVISIBLE
		binding.musicPlayerExpand.ivPause.visibility = View.INVISIBLE
	}

	/**
	 * Enabled the pause button and disables the play button for the small and large music player.
	 */
	private fun enablePauseButton() {
		binding.musicPlayerSmall.ivPause.visibility = View.VISIBLE
		binding.musicPlayerExpand.ivPause.visibility = View.VISIBLE

		binding.musicPlayerSmall.ivPlay.visibility = View.INVISIBLE
		binding.musicPlayerExpand.ivPlay.visibility = View.INVISIBLE
	}

	/**
	 * Loads the album art into the small and large music player
	 *
	 * 	@param mediaContentUri Location of the media item
	 */
	private fun setAlbumIconView(mediaContentUri: Uri?) {
		with(AlbumArtHelper.getAlbumArtBitmap(mediaContentUri, requireContext())) {
			Glide.with(requireContext()).load(this).into(binding.musicPlayerSmall.ivAlbum)
			Glide.with(requireContext()).load(this).into(binding.musicPlayerExpand.ivAlbum)
		}
	}

	/**
	 * Sets the background for the parent activity and the fragment to DarkGrey if [mediaContentUri] is null.
	 * If not null the background will be a blurred image of the [mediaContentUri] album art.
	 *
	 * @param mediaContentUri Location of the media item
	 */
	private fun setBackgroundView(mediaContentUri: Uri?) {
		if (mediaContentUri != null) {
			BitmapHelper.blurAlbumArt(mediaContentUri, requireContext()).also {
				binding.musicPlayerExpand.root.background = BitmapDrawable(resources, it)
				binding.musicPlayerSmall.backGroundLine.background = BitmapHelper.gradientFromBitmap(it)
				(activity as? MainActivity)?.getRootView()?.background = BitmapDrawable(resources, it)
			}
		} else { // If no media is played set all backgrounds to dark grey.
			ContextCompat.getColor(requireContext(), R.color.darkGrey).also {
				view?.setBackgroundColor(it)
				binding.musicPlayerSmall.backGroundLine.setBackgroundColor(it)
				binding.musicPlayerExpand.root.setBackgroundColor(it)
				if (activity is MainActivity) {
					(activity as MainActivity).getRootView().setBackgroundColor(it)
				}
			}

		}
	}

	/**
	 * Start the [LyricsActivity] and send the current artist and title via the intent extras
	 */
	private fun navigateToLyrics() {
		viewModel.currentTrack.value?.let {
			val intent = Intent(context!!, LyricsActivity::class.java)
			intent.putExtra(LyricsActivity.EXTRA_ARTIST, it.description.subtitle)
			intent.putExtra(LyricsActivity.EXTRA_TITLE, it.description.title)
			(activity!! as BaseActivity).startActivity(intent)
		}
	}

	fun setSlidingOffsetAlpha(slideOffset: Float) {
		binding.musicPlayerSmall.root.alpha = 1f - slideOffset
		binding.musicPlayerExpand.root.alpha = slideOffset
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<MusicPlayerViewModel> = MusicPlayerViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_music_player
}
