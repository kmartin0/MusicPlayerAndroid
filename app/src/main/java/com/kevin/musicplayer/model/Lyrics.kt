package com.kevin.musicplayer.model

import com.google.gson.annotations.SerializedName

data class Lyrics(@SerializedName("lyrics") val text: String)