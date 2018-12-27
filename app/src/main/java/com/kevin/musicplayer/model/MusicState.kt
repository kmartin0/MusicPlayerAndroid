package com.kevin.musicplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class MusicState : Parcelable {
	PLAYING, PAUSING
}