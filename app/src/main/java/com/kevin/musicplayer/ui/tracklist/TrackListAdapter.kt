package com.kevin.musicplayer.ui.tracklist

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.kevin.musicplayer.R

class TrackListAdapter(private val trackList: List<MediaBrowserCompat.MediaItem>,
					   private val onTrackClick: (MediaBrowserCompat.MediaItem) -> Unit)
	: androidx.recyclerview.widget.RecyclerView.Adapter<TrackListAdapter.ViewHolder>(), SectionTitleProvider {

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

		Glide.with(context)
				.load(albumUri)
				.listener(object : RequestListener<Drawable> {
					override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
						Handler().post { Glide.with(context).load(R.drawable.ic_disc).into(holder.ivAlbum) }
						return false
					}

					override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
						return false
					}
				})
				.into(holder.ivAlbum)

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

	class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
		val ivAlbum: ImageView = itemView.findViewById(R.id.ivAlbum)
		val tvTitle: TextView = itemView.findViewById(R.id.tvTrack)
		val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
		val clRoot: ConstraintLayout = itemView.findViewById(R.id.clRoot)
	}
}