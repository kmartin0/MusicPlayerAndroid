package com.kevin.musicplayer.ui.home

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kevin.musicplayer.base.BaseViewModel
import com.kevin.musicplayer.model.Track
import com.kevin.musicplayer.repository.AudioMediaRepository

class HomeViewModel(application: Application) : BaseViewModel(application) {

    private var audioMediaRepository = AudioMediaRepository(application.applicationContext)
    var songs: LiveData<List<Track>>

    init {
        songs = audioMediaRepository.getAllTracks()
    }

}