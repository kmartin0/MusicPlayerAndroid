package com.kevin.musicplayer.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentHomeBinding
import com.kevin.musicplayer.model.Track
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : BaseMVVMFragment<FragmentHomeBinding, HomeViewModel>() {

	private lateinit var trackListAdapter: TrackListAdapter
	private val songList = ArrayList<Track>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		trackListAdapter = TrackListAdapter(songList)
		val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

		view.rvSongList.layoutManager = layoutManager
		view.rvSongList.adapter = trackListAdapter
		view.fastScroller.setRecyclerView(view.rvSongList)
		viewModel.songs.observe(this, Observer { onDataSetChanged(it) })
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

	override fun getLayoutId(): Int = R.layout.fragment_home
}