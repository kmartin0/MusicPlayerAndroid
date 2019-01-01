package com.kevin.musicplayer.ui.tracklist

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.kevin.musicplayer.R

class TrackListAdapter(private val trackList: List<MediaBrowserCompat.MediaItem>,
					   private val onTrackClick: (MediaBrowserCompat.MediaItem) -> Unit)
	: RecyclerView.Adapter<TrackListAdapter.ViewHolder>(), SectionTitleProvider {

	private lateinit var context: Context
	private var currentTrackId: String? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		context = parent.context
		return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_song, parent, false))
	}

	override fun getItemCount(): Int {
		return trackList.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val albumUri = trackList[position].description.extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
		val trackTitle = trackList[position].description.title?.toString()
		val trackArtist = trackList[position].description.subtitle?.toString()

		if (albumUri != null) Glide.with(context).load(albumUri).into(holder.ivAlbum)
		else Glide.with(context).load(R.drawable.ic_disc).into(holder.ivAlbum)

		holder.tvTitle.text = trackTitle
		holder.tvArtist.text = trackArtist
		holder.clRoot.setOnClickListener { onTrackClick(trackList[position]) }
		if (trackList[position].description.mediaId == currentTrackId) {
			holder.clRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
		} else {
			holder.clRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
		}
	}

	fun setCurrentTrack(track: MediaMetadataCompat?) {
		currentTrackId = track?.description?.mediaId
		notifyDataSetChanged()
	}

	override fun getSectionTitle(position: Int): String? {
		val title = trackList[position].description.title?.toString()
		return if (title != null) title.toUpperCase()[0].toString() else null
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val ivAlbum: ImageView = itemView.findViewById(R.id.ivAlbum)
		val tvTitle: TextView = itemView.findViewById(R.id.tvTrack)
		val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
		val clRoot: ConstraintLayout = itemView.findViewById(R.id.clRoot)
	}
}