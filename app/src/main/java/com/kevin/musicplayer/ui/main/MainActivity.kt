package com.kevin.musicplayer.ui.main

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityMainBinding
import com.kevin.musicplayer.ui.tracklist.TrackListFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_music_player.view.*

class MainActivity : BaseMVVMActivity<ActivityMainBinding, MainViewModel>(), SlidingUpPanelLayout.PanelSlideListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		init()
		actionBar?.hide()
	}

	/**
	 * Adds the [TrackListFragment] to the container.
	 * Sets up the sliding panel in it's collapsed state and adds the slide listener.
	 */
	private fun init() {
		addFragment(R.id.fragmentContainer, TrackListFragment())
		slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
		slidingLayout.addPanelSlideListener(this)
	}

	/**
	 * When the panel is sliding upwards the small music player will be faded out and the large
	 * music player will be faded in. When sliding downwards the same will happen in opposite
	 * direction.
	 */
	override fun onPanelSlide(panel: View?, slideOffset: Float) {
		musicPlayerFragment.musicPlayerSmall.alpha = 1f - slideOffset
		musicPlayerFragment.musicPlayerExpand.alpha = slideOffset
	}

	override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {

	}

	override fun getLayoutId(): Int = R.layout.activity_main

	override fun getActivityTitle(): String = getString(R.string.app_name)

	override fun getVMClass(): Class<MainViewModel> = MainViewModel::class.java

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	fun getRootView(): ConstraintLayout = clRootMainActivity

	companion object {
	}
}