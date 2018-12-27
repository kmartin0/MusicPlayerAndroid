package com.kevin.musicplayer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "queue_table")
data class QueueTrack(
		var track: Track,
		var state: MusicState = MusicState.PAUSING,
		var currentTrack: Boolean = false
) : Parcelable {
	@PrimaryKey(autoGenerate = true)
	var queueTrackId: Int = 0

//	companion object {
//		@Parcelize
//		enum class MusicState : Parcelable {
//			PLAYING, PAUSING
//		}
//	}
}