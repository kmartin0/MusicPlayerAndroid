package com.kevin.musicplayer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "album_table")
data class Album(
		var title: String?,
		var art: String?,
		@PrimaryKey
		var id: String?,
		var artist: String?,
		var year: String?,
		var numberOfSongs: String?
) : Parcelable