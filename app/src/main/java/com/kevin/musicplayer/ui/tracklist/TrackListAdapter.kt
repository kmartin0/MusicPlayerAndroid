package com.kevin.musicplayer.ui.tracklist

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.kevin.musicplayer.R
import com.kevin.musicplayer.util.AlbumArtHelper
import java.util.*

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
		val mediaItem = trackList[position]

		Glide.with(context)
				.load(AlbumArtHelper.getAlbumArtBitmap(mediaItem.description.mediaUri, context))
				.into(holder.ivAlbum)

		holder.tvTitle.text = mediaItem.description.title
		holder.tvArtist.text = mediaItem.description.subtitle
		holder.clRoot.setOnClickListener { onTrackClick(trackList[position]) }
		if (mediaItem.description.mediaId == currentTrackId) {
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
		return if (title != null) title.toUpperCase(Locale.getDefault())[0].toString() else null
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val ivAlbum: ImageView = itemView.findViewById(R.id.ivAlbum)
		val tvTitle: TextView = itemView.findViewById(R.id.tvTrack)
		val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
		val clRoot: ConstraintLayout = itemView.findViewById(R.id.clRoot)
	}
}