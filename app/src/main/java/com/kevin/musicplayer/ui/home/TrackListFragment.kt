package com.kevin.musicplayer.ui.home

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentTrackListBinding
import com.kevin.musicplayer.model.Track
import com.kevin.musicplayer.service.MediaPlayerService
import com.kevin.musicplayer.service.MediaPlayerService.Companion.ACTION_PLAY
import com.kevin.musicplayer.service.MediaPlayerService.Companion.EXTRA_TRACK
import com.kevin.musicplayer.ui.main.MainActivity
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
		trackListAdapter = TrackListAdapter(songList) { onTrackClicked(it) }
		rvSongList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		rvSongList.adapter = trackListAdapter
		fastScroller.setRecyclerView(rvSongList)
	}

	private fun initObservers() {
		showLoading(true)
		viewModel.songs.observe(this, Observer { Log.i("TAGZ", "Dataset Changed!"); onDataSetChanged(it); showLoading(false) })
		viewModel.mediaPlayerManager.currentTrack.observe(this, Observer {
			if (it == null) trackListAdapter.setCurrentTrack(null) else trackListAdapter.setCurrentTrack(it.track)
			trackListAdapter.notifyDataSetChanged()
		})
	}

	private fun onDataSetChanged(tracks: List<Track>?) {
		songList.clear()
		tracks?.let { songList.addAll(tracks) }
		trackListAdapter.notifyDataSetChanged()
	}

	private fun onTrackClicked(track: Track) {
		val i = Intent(context, MediaPlayerService::class.java)
		i.action = ACTION_PLAY
		i.putExtra(EXTRA_TRACK, track)
		activity?.startService(i)
	}

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<HomeViewModel> = HomeViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_track_list

}