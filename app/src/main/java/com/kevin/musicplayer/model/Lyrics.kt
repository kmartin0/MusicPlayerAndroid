package com.kevin.musicplayer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "lyrics_table")
data class Lyrics(@SerializedName("lyrics") @PrimaryKey val text: String)