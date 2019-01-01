package com.kevin.musicplayer.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityMainBinding
import com.kevin.musicplayer.ui.tracklist.TrackListFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_music_player.*


class MainActivity : BaseMVVMActivity<ActivityMainBinding, MainViewModel>(), SlidingUpPanelLayout.PanelSlideListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (checkPermissions()) {
			init()
		}
	}

	private fun init() {
		musicPlayerFragment.musicPlayerExpand.alpha = 0f
		addFragment(R.id.fragmentContainer, TrackListFragment())
		slidingLayout.addPanelSlideListener(this)
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

	fun getRootView() : ConstraintLayout = clRootMainActivity

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

	override fun onPanelSlide(panel: View?, slideOffset: Float) {
		musicPlayerFragment.musicPlayerSmall.alpha = 1f - slideOffset
		musicPlayerFragment.musicPlayerExpand.alpha = slideOffset
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