package com.kevin.musicplayer.ui.home

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
	override fun getItem(position: Int): Fragment? {
		return when (position) {
			0 -> TrackListFragment()
			1 -> TrackListFragment()
			2 -> TrackListFragment()
			else -> null
		}
	}

	override fun getCount(): Int {
		return 3
	}

	override fun getPageTitle(position: Int): CharSequence? {
		return when (position) {
			0 -> "Tracks"
			1 -> "Artists"
			2 -> "Albums"
			else -> null
		}
	}
}