package com.kevin.musicplayer.ui.tracklist

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentTrackListBinding

class TrackListFragment : BaseMVVMFragment<FragmentTrackListBinding, TrackListViewModel>() {

	private lateinit var trackListAdapter: TrackListAdapter
	private val songList = ArrayList<MediaBrowserCompat.MediaItem>()

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initTrackListRv()
		initObservers()
	}

	/**
	 * Observe [TrackListViewModel.trackList] and update the [trackListAdapter] track list
	 * Observe [TrackListViewModel.currentTrack] and update the [trackListAdapter] current track
	 */
	private fun initObservers() {
		viewModel.trackList.observe(viewLifecycleOwner, { onDataSetChanged(it) })
		viewModel.currentTrack.observe(viewLifecycleOwner, { trackListAdapter.setCurrentTrack(it) })
	}

	/**
	 * Initializes the [trackListAdapter] and song list with the fast scroller
	 */
	private fun initTrackListRv() {
		trackListAdapter = TrackListAdapter(songList) { onTrackClicked(it) }
		binding.rvSongList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		binding.rvSongList.adapter = trackListAdapter
//		binding.fastScroller.setRecyclerView(binding.rvSongList)
	}

	/**
	 * Clears the [songList], adds all [tracks] and notifies the [trackListAdapter] about the data changes
	 */
	private fun onDataSetChanged(tracks: List<MediaBrowserCompat.MediaItem>?) {
		songList.clear()
		tracks?.let { songList.addAll(tracks) }
		trackListAdapter.notifyDataSetChanged()
	}

	private fun onTrackClicked(track: MediaBrowserCompat.MediaItem) {
		viewModel.play(track)
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<TrackListViewModel> = TrackListViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_track_list

}