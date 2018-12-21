package com.kevin.musicplayer.database.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevin.musicplayer.model.Album
import com.kevin.musicplayer.model.Artist

class MusicTypeConverters {
	@TypeConverter
	fun fromAlbum(album: Album?): String {
		return Gson().toJson(album)
	}

	@TypeConverter
	fun toAlbum(value: String): Album {
		val type = object : TypeToken<Album>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	fun fromArtist(artist: Artist?): String {
		return Gson().toJson(artist)
	}

	@TypeConverter
	fun toArtist(value: String): Artist {
		val type = object : TypeToken<Artist>() {}.type
		return Gson().fromJson(value, type)
	}
}
