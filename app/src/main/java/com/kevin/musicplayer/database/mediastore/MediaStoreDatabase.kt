package com.kevin.musicplayer.database.mediastore

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.kevin.musicplayer.model.Album
import com.kevin.musicplayer.model.Artist


class MediaStoreDatabase(private val context: Context) {

	/**
	 * @return a list of [MediaBrowserCompat.MediaItem] objects from all the tracks in the
	 * MediaStore Database
	 */
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
						.setMediaUri(ContentUris.withAppendedId(
								MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								tracksCursor.getLong(tracksCursor.getColumnIndex(MediaStore.Audio.Media._ID))
						))
						.build()

				trackList.add(MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
				tracksCursor.moveToNext()
			}
			tracksCursor.close()
		}
		return trackList
	}

	/**
	 * @return a list of [Artist] objects from all the artists in the MediaStore Database
	 */
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

	/**
	 * @return a list of [Album] objects from all the albums in the MediaStore Database
	 */
	fun getAllAlbums(): List<Album> {
		val albumCursor = MediaStoreHelper.getAlbumsCursor(context)
		val albumList = ArrayList<Album>()

		if (albumCursor != null && albumCursor.moveToFirst() && albumCursor.count > 0) {
			while (!albumCursor.isAfterLast) {
//				Uri.parse("content://media/external/audio/albumart"),
				val albumArtUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					ContentUris.withAppendedId(
							MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
							albumCursor.getLong(albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID))
					)
				} else {
					val deprecatedAlbumArt = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
					if (deprecatedAlbumArt != null) Uri.parse(deprecatedAlbumArt) else Uri.EMPTY
				}
Log.i("TAGZ", albumArtUri.toString())
				val album = Album(
						albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
						albumArtUri.toString(),
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