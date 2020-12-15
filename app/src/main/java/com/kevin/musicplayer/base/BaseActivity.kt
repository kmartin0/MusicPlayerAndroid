package com.kevin.musicplayer.base

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kevin.musicplayer.R

abstract class BaseActivity : AppCompatActivity() {

	/**
	 * Initialize the layout and title
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (inflateView()) setContentView(getLayoutId())
		title = getActivityTitle()
	}

	/**
	 * Displays a loading indicator based on [visibility]
	 */
	fun showLoading(visibility: Boolean) {
		findViewById<ProgressBar>(R.id.progressBar)?.visibility = if (visibility) View.VISIBLE else View.GONE
	}

	/**
	 * Adds a [Fragment] to this activity's layout.
	 *
	 * @param containerViewId The container view to where add the fragment.
	 * @param fragment The fragment to be added.
	 */
	fun addFragment(containerViewId: Int, fragment: Fragment) {
		val fragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.replace(containerViewId, fragment)
		fragmentTransaction.commit()
	}

	@LayoutRes
	abstract fun getLayoutId(): Int

	abstract fun getActivityTitle(): String

	protected open fun inflateView(): Boolean = true
}