package com.kevin.musicplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.content.ContextCompat
import com.kevin.musicplayer.R

class AlbumArtHelper {
	companion object {
		fun getAlbumArtBitmap(mediaContentUri: Uri?, context: Context, inSampleSize: Int = 1): Bitmap {
			val options = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize; inMutable = true }
			var albumArt: Bitmap = BitmapHelper.drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_disc)!!, inSampleSize)

			if (mediaContentUri != null) {
				try {
					val mediaMetadataRetriever = MediaMetadataRetriever()
					mediaMetadataRetriever.setDataSource(context.contentResolver.openFileDescriptor(mediaContentUri, "r")!!.fileDescriptor)

					val embeddedPicture = mediaMetadataRetriever.embeddedPicture

					albumArt = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size, options)
				} catch (ex: Exception) {
					// Do nothing when an exception occurs, will return the default disc image.
				}
			}

			return albumArt
		}
	}
}