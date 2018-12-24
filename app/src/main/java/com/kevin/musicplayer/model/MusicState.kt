package com.kevin.musicplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MusicState(
		var track: Track?,
		var state: MusicState
) : Parcelable {
	companion object {
		@Parcelize
		enum class MusicState : Parcelable {
			PLAYING, PAUSING
		}
	}
}