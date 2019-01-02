package com.kevin.musicplayer.ui.lyrics

import android.os.Bundle
import android.view.MenuItem
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityLyricsBinding
import kotlinx.android.synthetic.main.activity_lyrics.*

class LyricsActivity : BaseMVVMActivity<ActivityLyricsBinding, LyricsViewModel>() {

	companion object {
		const val EXTRA_ARTIST = "EXTRA_ARTIST"
		const val EXTRA_TITLE = "EXTRA_TITLE"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initFromExtras()
		initActionBar()
	}

	/**
	 * Initializes the actionbar
	 */
	private fun initActionBar() {
		setActionBar(toolbarLyrics)
		actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
		actionBar?.setDisplayHomeAsUpEnabled(true)
	}

	/**
	 * If extras are sent, gets the artist and title sent in extras and tells the view model
	 * to get the lyrics.
	 */
	private fun initFromExtras() {
		intent.extras?.let {
			val artist = it.getString(EXTRA_ARTIST)
			val title = it.getString(EXTRA_TITLE)
			if (artist != null && title != null) {
				viewModel.setData(artist, title)
				viewModel.getLyrics()
			}
		}
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		return when (item?.itemId) {
			android.R.id.home -> {
				onBackPressed()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun getActivityTitle(): String = getString(R.string.lyrics)

	override fun initViewModelBinding() {
		binding.viewModel = viewModel
		binding.setLifecycleOwner(this)
	}

	override fun getVMClass(): Class<LyricsViewModel> = LyricsViewModel::class.java

	override fun getLayoutId(): Int = R.layout.activity_lyrics
}