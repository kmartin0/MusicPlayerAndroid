package com.kevin.musicplayer.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle

abstract class BaseMVVMActivity<T : ViewDataBinding, V : ViewModel> : BaseActivity() {
    protected lateinit var binding: T
    protected lateinit var viewModel: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        viewModel = ViewModelProviders.of(this).get(getVMClass())
        initViewModelBinding()
    }

    abstract fun initViewModelBinding()

    abstract fun getVMClass(): Class<V>

}