package com.kevin.musicplayer.ui.lyrics

import android.os.Bundle
import android.view.MenuItem
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityLyricsBinding
import kotlinx.android.synthetic.main.activity_lyrics.*

class LyricsActivity : BaseMVVMActivity<ActivityLyricsBinding, LyricsViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFromExtras()
        setActionBar(toolbarLyrics)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initFromExtras() {
        intent.extras?.let {
            val artist = it.getString("EXTRA_ARTIST")
            val title = it.getString("EXTRA_TITLE")
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

    override fun getActivityTitle(): String = "Lyrics"

    override fun initViewModelBinding() {
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
    }

    override fun getVMClass(): Class<LyricsViewModel> = LyricsViewModel::class.java

    override fun getLayoutId(): Int = R.layout.activity_lyrics

}