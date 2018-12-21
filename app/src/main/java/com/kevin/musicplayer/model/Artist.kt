package com.kevin.musicplayer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "artist_table")
data class Artist(
		var name: String,
		@PrimaryKey
		var id: String,
		var numberOfAlbums: String,
		var numberOfTracks: String
) : Parcelable