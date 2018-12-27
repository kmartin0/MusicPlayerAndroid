package com.kevin.musicplayer.database.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevin.musicplayer.model.*

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

	@TypeConverter
	fun fromTrack(track: Track?): String {
		return Gson().toJson(track)
	}

	@TypeConverter
	fun toTrack(value: String): Track {
		val type = object : TypeToken<Track>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	fun fromMusicState(musicState: MusicState): String {
		return musicState.toString()
	}

	@TypeConverter
	fun toMusicState(value: String): MusicState {
		return when (value) {
			MusicState.PAUSING.toString() -> MusicState.PAUSING
			MusicState.PLAYING.toString() -> MusicState.PLAYING
			else -> MusicState.PAUSING
		}
	}
}
