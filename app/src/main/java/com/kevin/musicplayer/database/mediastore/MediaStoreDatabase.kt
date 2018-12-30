package com.kevin.musicplayer.database.mediastore

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.kevin.musicplayer.model.Album
import com.kevin.musicplayer.model.Artist
import com.kevin.musicplayer.model.Track

class MediaStoreDatabase(private val context: Context) {

	fun getAllTracks(): List<MediaBrowserCompat.MediaItem> {
		val tracksCursor = MediaStoreHelper.getTracksCursor(context)

		val artistList = getAllArtists()
		val albumList = getAllAlbums()
		val trackList = ArrayList<MediaBrowserCompat.MediaItem>()

		if (tracksCursor != null && tracksCursor.moveToFirst() && tracksCursor.count > 0) {
			while (!tracksCursor.isAfterLast) {

				val album = albumList.find { album -> album.id == tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)) }
				val artist = artistList.find { artist -> artist.id == tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)) }

				val extras = Bundle()
				extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, album?.art)

				val desc = MediaDescriptionCompat.Builder()
						.setMediaId(tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media._ID)))
						.setTitle(tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)))
						.setSubtitle(artist?.name)
						.setDescription(album?.title)
						.setExtras(extras)
						.setMediaUri(Uri.parse(tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.DATA))))
						.build()

				trackList.add(MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
				tracksCursor.moveToNext()
			}
			tracksCursor.close()
		}
		return trackList
	}

	fun getLittleWing(): Track? {
		val tracksCursor = MediaStoreHelper.getLittleWingCursor(context)

		val artistList = getAllArtists()
		val albumList = getAllAlbums()
		var littleWing: Track? = null

		if (tracksCursor != null && tracksCursor.moveToFirst() && tracksCursor.count > 0) {
			while (!tracksCursor.isAfterLast) {

				val album = albumList.find { album -> album.id == tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)) }
				val artist = artistList.find { artist -> artist.id == tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)) }

				val track = Track(
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media._ID)),
						album,
						artist,
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.TRACK)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.YEAR)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.SIZE)),
						tracksCursor.getString(tracksCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
				)
				littleWing = track
				tracksCursor.moveToNext()
			}
			tracksCursor.close()
		}
		return littleWing
	}

	fun getAllArtists(): List<Artist> {
		val artistsCursor = MediaStoreHelper.getArtistsCursor(context)
		val artistList = ArrayList<Artist>()

		if (artistsCursor != null && artistsCursor.moveToFirst() && artistsCursor.count > 0) {
			while (!artistsCursor.isAfterLast) {
				val artist = Artist(
						artistsCursor.getString(artistsCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)),
						artistsCursor.getString(artistsCursor.getColumnIndex(MediaStore.Audio.Artists._ID)),
						artistsCursor.getString(artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)),
						artistsCursor.getString(artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))
				)
				artistList.add(artist)
				artistsCursor.moveToNext()
			}
			artistsCursor.close()
		}
		return artistList
	}

	fun getAllAlbums(): List<Album> {
		val albumCursor = MediaStoreHelper.getAlbumsCursor(context)
		val albumList = ArrayList<Album>()

		if (albumCursor != null && albumCursor.moveToFirst() && albumCursor.count > 0) {
			while (!albumCursor.isAfterLast) {
				val album = Album(
						albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
						albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)),
						albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID)),
						albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
						albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)),
						albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS))
				)
				albumList.add(album)
				albumCursor.moveToNext()
			}
			albumCursor.close()
		}
		return albumList
	}

}