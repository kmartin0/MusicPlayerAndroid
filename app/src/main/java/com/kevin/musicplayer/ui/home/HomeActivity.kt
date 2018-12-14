package com.kevin.musicplayer.ui.home

import android.os.Bundle
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMActivity
import com.kevin.musicplayer.databinding.ActivityHomeBinding

class HomeActivity : BaseMVVMActivity<ActivityHomeBinding, HomeViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun getActivityTitle(): String = "Music Player"

    override fun getVMClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun initViewModelBinding() {
        binding.viewModel = viewModel
    }

}