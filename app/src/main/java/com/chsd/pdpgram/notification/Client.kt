package com.chsd.pdpgram.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}