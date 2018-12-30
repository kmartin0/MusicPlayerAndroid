package com.kevin.musicplayer

import android.os.Bundle
import com.kevin.musicplayer.base.BaseActivity
import com.kevin.musicplayer.util.MediaSessionConnection

class MusicActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		MediaSessionConnection(this)
	}

	override fun getLayoutId(): Int = R.layout.music_player_expand

	override fun getActivityTitle(): String = "Music"

}