package com.kevin.musicplayer.ui.home

import android.app.Application
import android.arch.lifecycle.LiveData
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.model.Track
import com.kevin.musicplayer.repository.AudioMediaRepository
import com.kevin.musicplayer.util.MediaPlayerManager

class HomeViewModel(application: Application) : BaseViewModel(application) {

	private var audioMediaRepository = AudioMediaRepository(application.applicationContext)
	var songs: LiveData<List<Track>>

	init {
		songs = audioMediaRepository.getAllTracks()
	}
}
