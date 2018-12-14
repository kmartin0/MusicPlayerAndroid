package com.kevin.musicplayer.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityMainBinding
import com.kevin.musicplayer.ui.home.HomeFragment
import com.kevin.musicplayer.ui.search.SearchFragment
import com.kevin.musicplayer.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMVVMActivity<ActivityMainBinding, MainViewModel>(), BottomNavigationView.OnNavigationItemSelectedListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bottomNavBar.setOnNavigationItemSelectedListener(this)
		addFragment(R.id.fragmentContainer, HomeFragment())
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_home -> addFragment(R.id.fragmentContainer, HomeFragment())
			R.id.action_search -> addFragment(R.id.fragmentContainer, SearchFragment())
			R.id.action_settings -> addFragment(R.id.fragmentContainer, SettingsFragment())
		}
		return true
	}

	override fun getLayoutId(): Int = R.layout.activity_main

	override fun getActivityTitle(): String = "My Music Player"

	override fun getVMClass(): Class<MainViewModel> = MainViewModel::class.java

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

}