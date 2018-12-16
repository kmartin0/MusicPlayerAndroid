package com.kevin.musicplayer.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import android.view.View
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityMainBinding
import com.kevin.musicplayer.ui.home.HomeFragment
import com.kevin.musicplayer.ui.search.SearchFragment
import com.kevin.musicplayer.ui.settings.SettingsFragment
import com.kevin.musicplayer.util.DialogHelper
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseMVVMActivity<ActivityMainBinding, MainViewModel>(), BottomNavigationView.OnNavigationItemSelectedListener, SlidingUpPanelLayout.PanelSlideListener {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		layMusicExpand.alpha = 0f
		bottomNavBar.setOnNavigationItemSelectedListener(this)
		addFragment(R.id.fragmentContainer, HomeFragment())
		slidingLayout.addPanelSlideListener(this)
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_home -> addFragment(R.id.fragmentContainer, HomeFragment())
			R.id.action_search -> addFragment(R.id.fragmentContainer, SearchFragment())
			R.id.action_settings -> addFragment(R.id.fragmentContainer, SettingsFragment())
		}
		return true
	}

	fun onAlbumClick(view: View) {
		DialogHelper.showToast(this, "Album Clicked")
	}

	override fun onPanelSlide(panel: View?, slideOffset: Float) {
		layMusicSmall.alpha = 1f - slideOffset
		layMusicExpand.alpha = slideOffset
	}

	override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {

	}

	override fun getLayoutId(): Int = R.layout.activity_main

	override fun getActivityTitle(): String = "My Music Player"

	override fun getVMClass(): Class<MainViewModel> = MainViewModel::class.java

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

}