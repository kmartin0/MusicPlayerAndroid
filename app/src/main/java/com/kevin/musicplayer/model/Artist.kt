package com.kevin.musicplayer.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "artist_table")
data class Artist(
		var name: String,
		@PrimaryKey
		var id: String,
		var numberOfAlbums: String,
		var numberOfTracks: String
) : Parcelable