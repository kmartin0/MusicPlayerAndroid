package com.kevin.musicplayer.util

import android.content.Context
import android.widget.Toast

class DialogHelper {
	companion object {
		fun showToast(context: Context, message: String) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show()
		}
	}
}