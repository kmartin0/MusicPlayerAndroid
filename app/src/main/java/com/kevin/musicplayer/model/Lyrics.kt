package com.kevin.musicplayer.model

import androidx.room.Entity

@Entity(tableName = "lyrics_table", primaryKeys = ["title", "artist"])
data class Lyrics(
		val title: String,
		val artist: String,
		var text: String?)