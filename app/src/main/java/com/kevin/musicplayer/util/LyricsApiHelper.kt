package com.kevin.musicplayer.util

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

class LyricsApiHelper {
    companion object {
        fun readErrorMessage(errorBody: ResponseBody): String {
            return try {
                val errorJson = JSONObject(errorBody.string())
                if (errorJson.has("error")) errorJson.getString("error")
                else "Something went wrong while reading the lyrics."
            } catch (e: JSONException) {
                e.printStackTrace()
                "Something went wrong while reading the lyrics."
            }
        }
    }
}