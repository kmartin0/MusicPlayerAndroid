package com.kevin.musicplayer.ui.home

import com.kevin.musicplayer.R
import com.kevin.musicplayer.base.BaseMVVMFragment
import com.kevin.musicplayer.databinding.FragmentHomeBinding


class HomeFragment : BaseMVVMFragment<FragmentHomeBinding, HomeViewModel>() {
	override fun initViewModelBinding() {
		binding.viewModel = viewModel
	}

	override fun getVMClass(): Class<HomeViewModel> = HomeViewModel::class.java

	override fun getLayoutId(): Int = R.layout.fragment_home

//	override fun onCreateView(
//			inflater: LayoutInflater,
//			container: ViewGroup?,
//			savedInstanceState: Bundle?
//	): View {
//		// Inflate the layout for this fragment
//		return inflater.inflate(R.layout.fragment_home, container, false)
//	}

//	override fun onCreate(savedInstanceState: Bundle?) {
//		super.onCreate(savedInstanceState)
//		AudioMediaRepository(context!!)
//	}


}