package com.kevin.musicplayer.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseMVVMFragment<T : ViewDataBinding, V : ViewModel> : BaseFragment() {

	protected lateinit var binding: T
	protected lateinit var viewModel: V

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
		viewModel = ViewModelProviders.of(this).get(getVMClass())
		initViewModelBinding()

		return binding.root
	}

	abstract fun initViewModelBinding()

	abstract fun getVMClass(): Class<V>

}
