package ru.practicum.android.diploma.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.data.Constants.BASE_URL_HH
import ru.practicum.android.diploma.data.network.HHApi
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.favorites.db.AppDatabase

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
