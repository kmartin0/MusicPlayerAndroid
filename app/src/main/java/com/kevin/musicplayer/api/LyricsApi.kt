package com.kevin.musicplayer.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class LyricsApi {
    companion object {

		private const val BASE_LYRICS_API_URL = "https://api.lyrics.ovh/v1/"

        fun create(): LyricsApiService {
            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()

            val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(BASE_LYRICS_API_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(LyricsApiService::class.java)
        }
    }
}