package ru.practicum.android.diploma.util

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// TODO Удалить после тестов

// Создаем логгер
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY // Уровень логирования
}

// Создаем OkHttpClient с логированием
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
