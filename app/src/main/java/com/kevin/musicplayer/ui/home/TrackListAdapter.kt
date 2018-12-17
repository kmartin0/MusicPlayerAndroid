package com.kevin.musicplayer.ui.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kevin.musicplayer.R
import com.kevin.musicplayer.model.Track

class TrackListAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<TrackListAdapter.ViewHolder>() {

	private lateinit var context: Context

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		context = parent.context
		return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_song, parent, false))
	}

	override fun getItemCount(): Int {
		return trackList.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val albumUri = trackList[position].album?.art
		val trackTitle = trackList[position].title
		val trackArtist = trackList[position].artist?.name

		if (albumUri != null) Glide.with(context).load(albumUri).into(holder.ivAlbum)
		else Glide.with(context).load(R.drawable.ic_album_placeholder).into(holder.ivAlbum)

		holder.tvTitle.text = trackTitle
		holder.tvArtist.text = trackArtist
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val ivAlbum: ImageView = itemView.findViewById(R.id.ivAlbum)
		val tvTitle: TextView = itemView.findViewById(R.id.tvSong)
		val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
	}
}