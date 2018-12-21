package com.kevin.musicplayer.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentTrackListBinding
import com.kevin.musicplayer.model.Track
import kotlinx.android.synthetic.main.fragment_track_list.*

class TrackListFragment : BaseMVVMFragment<FragmentTrackListBinding, HomeViewModel>() {

	private lateinit var trackListAdapter: TrackListAdapter
	private val songList = ArrayList<Track>()

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initTrackListRv()
		initObservers()
	}

	private fun initTrackListRv() {
		trackListAdapter = TrackListAdapter(songList)
		rvSongList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		rvSongList.adapter = trackListAdapter
		fastScroller.setRecyclerView(rvSongList)
	}

	private fun initObservers() {
		showLoading(true)
		viewModel.songs.observe(this, Observer { onDataSetChanged(it); showLoading(false) })
	}

	private fun onDataSetChanged(tracks: List<Track>?) {
		songList.clear()
		tracks?.let { songList.addAll(tracks) }
		trackListAdapter.notifyDataSetChanged()
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<HomeViewModel> = HomeViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_track_list

}