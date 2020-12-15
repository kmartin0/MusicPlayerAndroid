package com.kevin.musicplayer.database.mediastore

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore

class MediaStoreHelper {

	companion object {

		/**
		 * @return a [Cursor] object to retrieve all tracks from the MediaStore Database
		 */
		fun getTracksCursor(context: Context): Cursor? {
			// Uri maps to the table in the provider named table_name.
			val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

			// projection is an array of columns that should be included for each row retrieved.
			val projection = arrayOf(
					MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.ARTIST_ID,
					MediaStore.Audio.Media.BOOKMARK,
					MediaStore.Audio.Media.TRACK,
					MediaStore.Audio.Media.YEAR,
					MediaStore.Audio.Media.DATE_ADDED,
					MediaStore.Audio.Media.DATE_MODIFIED,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.MIME_TYPE,
					MediaStore.Audio.Media.SIZE,
					MediaStore.Audio.Media.TITLE,
			)

			// selection specifies the criteria for selecting rows.
			val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

			// sortOrder specifies the order in which rows appear in the returned Cursor.
			val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

			return context.contentResolver.query(
					uri,
					projection,
					selection,
					null,
					sortOrder
			)
		}

		/**
		 * @return a [Cursor] object to retrieve all albums from the MediaStore Database
		 */
		fun getAlbumsCursor(context: Context): Cursor? {

			// Uri maps to the table in the provider named table_name.
			val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

			// projection is an array of columns that should be included for each row retrieved.
			var projection = arrayOf(
					MediaStore.Audio.Albums.ALBUM,
					MediaStore.Audio.Albums._ID,
					MediaStore.Audio.Albums.ARTIST,
					MediaStore.Audio.Albums.FIRST_YEAR,
					MediaStore.Audio.Albums.NUMBER_OF_SONGS,
			)

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
				projection = projection.plus(MediaStore.Audio.Albums.ALBUM_ART)
			}

			return context.contentResolver.query(
					uri,
					projection,
					null,
					null,
					null
			)
		}

		/**
		 * @return a [Cursor] object to retrieve all artists from the MediaStore Database
		 */
		fun getArtistsCursor(context: Context): Cursor? {

			// Uri maps to the table in the provider named table_name.
			val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

			// projection is an array of columns that should be included for each row retrieved.
			val projection = arrayOf(
					MediaStore.Audio.Artists.ARTIST,
					MediaStore.Audio.Artists._ID,
					MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
					MediaStore.Audio.Artists.NUMBER_OF_TRACKS
			)

			return context.contentResolver.query(
					uri,
					projection,
					null,
					null,
					null
			)
		}
	}
}