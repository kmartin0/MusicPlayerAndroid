package com.kevin.musicplayer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseFragment

class SettingsFragment : BaseFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_settings, container, false)
	}

}