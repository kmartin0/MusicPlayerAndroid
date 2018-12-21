package com.kevin.musicplayer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "track_table")
data class Track(
		@PrimaryKey
		var id : String,
		var album: Album?,
		var artist: Artist?,
		var bookmark: String?,
		var trackNumber: String?,
		var year: String?,
		var data: String?,
		var dateAdded: String?,
		var dateModified: String?,
		var displayName: String?,
		var mimeType: String?,
		var size: String?,
		var title: String?
) : Parcelable
