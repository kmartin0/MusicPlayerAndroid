package com.kevin.musicplayer.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

abstract class BaseMVVMActivity<T : ViewDataBinding, V : BaseViewModel> : BaseActivity() {
	protected lateinit var binding: T
	protected lateinit var viewModel: V

	/**
	 * Setup the data binding and view model
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, getLayoutId())
		viewModel = ViewModelProvider(this).get(getVMClass())
		binding.lifecycleOwner = this
		initViewModelBinding()
	}

	abstract fun initViewModelBinding()

	abstract fun getVMClass(): Class<V>

	override fun inflateView(): Boolean = false

}