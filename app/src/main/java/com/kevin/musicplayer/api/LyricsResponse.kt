package com.kevin.musicplayer.api

import com.google.gson.annotations.SerializedName

data class LyricsResponse(@SerializedName("lyrics") val lyrics: String)