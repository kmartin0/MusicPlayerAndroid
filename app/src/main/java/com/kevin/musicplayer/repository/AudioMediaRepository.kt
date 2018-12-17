package com.kevin.musicplayer.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.provider.MediaStore
import com.kevin.musicplayer.model.Album
import com.kevin.musicplayer.model.Artist
import com.kevin.musicplayer.model.Track

class AudioMediaRepository(private val context: Context) {

	/**
	 * @return LiveData<List<Track>> containing the Tracks on the device.
	 */
	fun getAllTracks(): LiveData<List<Track>> {

		val tracks = ArrayList<Track>()
		val albums = getAlbums().value
		val artists = getArtists().value
		val tracksLiveData = MutableLiveData<List<Track>>().apply { value = tracks }

		// Uri maps to the table in the provider named table_name.
		val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

		// projection is an array of columns that should be included for each row retrieved.
		val projection = arrayOf(
				MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.ARTIST_ID,
				MediaStore.Audio.Media.BOOKMARK,
				MediaStore.Audio.Media.TRACK,
				MediaStore.Audio.Media.YEAR,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DATE_ADDED,
				MediaStore.Audio.Media.DATE_MODIFIED,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.MIME_TYPE,
				MediaStore.Audio.Media.SIZE,
				MediaStore.Audio.Media.TITLE
		)

		// selection specifies the criteria for selecting rows.
		val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

		// sortOrder specifies the order in which rows appear in the returned Cursor.
		val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

		val cursor = context.contentResolver.query(
				uri,
				projection,
				selection,
				null,
				sortOrder
		)

		if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
			while (!cursor.isAfterLast) {

				val album = albums?.find { album -> album.id == cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)) }
				val artist = artists?.find { artist -> artist.id == cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)) }

				val track = Track(
						album,
						artist,
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
				)
				tracks.add(track)
				cursor.moveToNext()
			}
			cursor.close()
		}
		return tracksLiveData
	}

	/**
	 * @return LiveData<List<Album>> containing the Albums on the device.
	 */
	fun getAlbums(): LiveData<List<Album>> {

		val albums = ArrayList<Album>()
		val albumsLiveData = MutableLiveData<List<Album>>().apply { value = albums }

		// Uri maps to the table in the provider named table_name.
		val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

		// projection is an array of columns that should be included for each row retrieved.
		val projection = arrayOf(
				MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ALBUM_ART,
				MediaStore.Audio.Albums._ID,
				MediaStore.Audio.Albums.ARTIST,
				MediaStore.Audio.Albums.FIRST_YEAR,
				MediaStore.Audio.Albums.NUMBER_OF_SONGS
		)

		val cursor = context.contentResolver.query(
				uri,
				projection,
				null,
				null,
				null
		)

		if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
			while (!cursor.isAfterLast) {
				val album = Album(
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS))
				)
				albums.add(album)
				cursor.moveToNext()
			}
			cursor.close()
		}
		return albumsLiveData
	}

	/**
	 * @return LiveData<List<Artist>> containing the Artists on the device.
	 */
	fun getArtists(): LiveData<List<Artist>> {

		val artists = ArrayList<Artist>()
		val artistsLiveData = MutableLiveData<List<Artist>>().apply { value = artists }

		// Uri maps to the table in the provider named table_name.
		val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

		// projection is an array of columns that should be included for each row retrieved.
		val projection = arrayOf(
				MediaStore.Audio.Artists.ARTIST,
				MediaStore.Audio.Artists._ID,
				MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
				MediaStore.Audio.Artists.NUMBER_OF_TRACKS
		)

		val cursor = context.contentResolver.query(
				uri,
				projection,
				null,
				null,
				null
		)


		if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
			while (!cursor.isAfterLast) {
				val artist = Artist(
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))
				)
				artists.add(artist)
				cursor.moveToNext()
			}
			cursor.close()
		}
		return artistsLiveData
	}
}


