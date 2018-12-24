package com.kevin.musicplayer.ui.home

import android.os.Bundle
import android.view.View
import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentHomeBinding
import com.kevin.musicplayer.util.DialogHelper
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : BaseMVVMFragment<FragmentHomeBinding, HomeViewModel>() {

    private lateinit var pagerAdapter: PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = PagerAdapter(childFragmentManager)
        view.pager.adapter = pagerAdapter
        view.pager.offscreenPageLimit = 2
        view.tabLayout.setupWithViewPager(view.pager)
    }

    override fun initViewModelBinding() {
        binding.viewModel = viewModel
    }

    override fun getVMClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_home
}