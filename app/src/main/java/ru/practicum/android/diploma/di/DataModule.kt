package ru.practicum.android.diploma.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.data.Constants.BASE_URL_HH
import ru.practicum.android.diploma.data.favorites.db.AppDatabase
import ru.practicum.android.diploma.data.search.network.HHApi
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.data.search.network.RetrofitNetworkClient

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

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.megaHH"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().favoriteVacancyDao() }
}
