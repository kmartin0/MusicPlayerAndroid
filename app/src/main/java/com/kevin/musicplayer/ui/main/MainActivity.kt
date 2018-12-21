package com.kevin.musicplayer.ui.main

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityMainBinding
import com.kevin.musicplayer.ui.home.TrackListFragment
import com.kevin.musicplayer.ui.search.SearchFragment
import com.kevin.musicplayer.ui.settings.SettingsFragment
import com.kevin.musicplayer.util.DialogHelper
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseMVVMActivity<ActivityMainBinding, MainViewModel>(), BottomNavigationView.OnNavigationItemSelectedListener, SlidingUpPanelLayout.PanelSlideListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkPermissions()) {
            init()
        }
    }

    private fun init() {
        layMusicExpand.alpha = 0f
        bottomNavBar.setOnNavigationItemSelectedListener(this)
        bottomNavBar.selectedItemId = R.id.action_home
        slidingLayout.addPanelSlideListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> addFragment(R.id.fragmentContainer, TrackListFragment())
            R.id.action_search -> addFragment(R.id.fragmentContainer, SearchFragment())
            R.id.action_settings -> addFragment(R.id.fragmentContainer, SettingsFragment())
        }
        return true
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    100)

            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    init()
                } else {
                    checkPermissions()
                }
                return
            }
        }
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