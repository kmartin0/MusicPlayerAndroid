package com.kevin.musicplayer.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle

abstract class BaseMVVMActivity<T : ViewDataBinding, V : BaseViewModel> : BaseActivity() {
	protected lateinit var binding: T
	protected lateinit var viewModel: V

	/**
	 * Setup the data binding and view model
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, getLayoutId())
		viewModel = ViewModelProviders.of(this).get(getVMClass())
		initViewModelBinding()
	}

	/**
	 * Display the loading indicator based on the [BaseViewModel.isLoading]
	 */
	fun initLoadingObserver() {
		viewModel.isLoading.observe(this, Observer { isLoading ->
			if (isLoading != null) showLoading(isLoading) else showLoading(false)
		})
	}

	abstract fun initViewModelBinding()

	abstract fun getVMClass(): Class<V>

	override fun inflateView(): Boolean = false

}