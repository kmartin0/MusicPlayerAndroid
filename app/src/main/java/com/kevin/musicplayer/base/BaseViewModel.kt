package com.kevin.musicplayer.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
	val isLoading = MutableLiveData<Boolean>()

	protected fun startLoading() {
		isLoading.value = true
	}

	protected fun stopLoading() {
		isLoading.value = false
	}

}