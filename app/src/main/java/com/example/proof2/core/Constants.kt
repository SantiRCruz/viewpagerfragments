package com.example.proof2.core

import okhttp3.OkHttpClient

object Constants {
    const val URL = "https://wsa2021.mad.hakta.pro/api"
    const val USER = "user"
    val okhttp = OkHttpClient.Builder().build()
}