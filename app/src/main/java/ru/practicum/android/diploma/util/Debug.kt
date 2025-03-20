package ru.practicum.android.diploma.util

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// Удалить после тестов

val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
