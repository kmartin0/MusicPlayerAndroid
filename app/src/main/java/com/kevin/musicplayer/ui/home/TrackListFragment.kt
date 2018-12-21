package com.kevin.musicplayer.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentTrackListBinding
import com.kevin.musicplayer.model.Track
import com.kevin.musicplayer.util.DialogHelper
import kotlinx.android.synthetic.main.fragment_track_list.*
import kotlinx.android.synthetic.main.fragment_track_list.view.*

class TrackListFragment : BaseMVVMFragment<FragmentTrackListBinding, HomeViewModel>() {

    private lateinit var trackListAdapter: TrackListAdapter
    private val songList = ArrayList<Track>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        trackListAdapter = TrackListAdapter(songList)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rvSongList.layoutManager = layoutManager
        rvSongList.adapter = trackListAdapter
        fastScroller.setRecyclerView(rvSongList)
        viewModel.songs.observe(this, Observer { onDataSetChanged(it)})
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