package com.kevin.musicplayer.util

import android.content.Context
import com.kevin.musicplayer.R
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

const val LYRICS_API_ERROR_KEY = "error"

class LyricsApiHelper {
	companion object {
		/**
		 * @return [String] containing the Error message from the LyricsApi
		 */
		fun readErrorMessage(errorBody: ResponseBody, context: Context): String {
			return try {
				val errorJson = JSONObject(errorBody.string())
				if (errorJson.has(LYRICS_API_ERROR_KEY)) errorJson.getString(LYRICS_API_ERROR_KEY)
				else context.getString(R.string.lyrics_api_error)
			} catch (e: JSONException) {
				e.printStackTrace()
				context.getString(R.string.lyrics_api_error)
			}
		}
	}
}