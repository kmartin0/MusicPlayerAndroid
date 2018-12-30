package com.kevin.musicplayer.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v7.widget.LinearLayoutManager
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentTrackListBinding
import kotlinx.android.synthetic.main.fragment_track_list.*

class TrackListFragment : BaseMVVMFragment<FragmentTrackListBinding, HomeViewModel>() {

	private lateinit var trackListAdapter: TrackListAdapter
	private val songList = ArrayList<MediaBrowserCompat.MediaItem>()

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initTrackListRv()
		initObservers()
	}

	private fun initObservers() {
		viewModel.trackList.observe(this, Observer { onDataSetChanged(it) })
		viewModel.currentTrack.observe(this, Observer { trackListAdapter.setCurrentTrack(it) })
	}

	private fun initTrackListRv() {
		trackListAdapter = TrackListAdapter(songList) { onTrackClicked(it) }
		rvSongList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		rvSongList.adapter = trackListAdapter
		fastScroller.setRecyclerView(rvSongList)
	}

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

	override fun getVMClass(): Class<HomeViewModel> = HomeViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_track_list

}