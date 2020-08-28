package com.kevin.musicplayer.ui.tracklist

import androidx.lifecycle.Observer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentTrackListBinding
import kotlinx.android.synthetic.main.fragment_track_list.*

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
		viewModel.trackList.observe(this, Observer { onDataSetChanged(it) })
		viewModel.currentTrack.observe(this, Observer { trackListAdapter.setCurrentTrack(it) })
	}

	/**
	 * Initializes the [trackListAdapter] and [rvSongList] with the [fastScroller]
	 */
	private fun initTrackListRv() {
		trackListAdapter = TrackListAdapter(songList) { onTrackClicked(it) }
		rvSongList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
		rvSongList.adapter = trackListAdapter
		fastScroller.setRecyclerView(rvSongList)
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