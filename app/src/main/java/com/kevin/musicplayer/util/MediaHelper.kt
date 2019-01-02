package com.kevin.musicplayer.util

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat

class MediaHelper {
	companion object {
		fun descriptionCompatToMetadataCompat(mediaDescriptionCompat: MediaDescriptionCompat): MediaMetadataCompat {
			return MediaMetadataCompat.Builder()
					.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaDescriptionCompat.mediaId)
					.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, mediaDescriptionCompat.title.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, mediaDescriptionCompat.subtitle.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mediaDescriptionCompat.description.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, mediaDescriptionCompat.extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
					.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaDescriptionCompat.mediaUri.toString())
					.build()
		}
	}
}