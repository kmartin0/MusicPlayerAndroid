package com.kevin.musicplayer.repository

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.kevin.musicplayer.database.mediastore.MediaStoreDatabase
import com.kevin.musicplayer.model.Track
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AudioMediaRepository(context: Context) {
    private val mediaStoreDatabase = MediaStoreDatabase(context)

    /**
     * @return LiveData<List<Track>> containing the Tracks on the device.
     */
    fun getAllTracks(): MutableLiveData<List<Track>> {
        val trackLiveData = MutableLiveData<List<Track>>()
        doAsync {
            val tracks = mediaStoreDatabase.getAllTracks()
            uiThread { trackLiveData.value = tracks }
        }
        return trackLiveData
    }
}


