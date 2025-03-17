package ru.practicum.android.diploma.di

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.data.Constants.BASE_URL_HH
import ru.practicum.android.diploma.data.network.HHApi
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient

val dataModule = module {
    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<HHApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL_HH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HHApi::class.java)
    }
}
