package ru.practicum.android.diploma.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.data.favorites.db.AppDatabase
import ru.practicum.android.diploma.data.search.network.HHApi
import ru.practicum.android.diploma.data.search.network.NetworkClient
import ru.practicum.android.diploma.data.search.network.RetrofitNetworkClient
import ru.practicum.android.diploma.util.okHttpClient
import ru.practicum.android.diploma.data.filter.impl.FilterStateStorageImpl
import ru.practicum.android.diploma.data.filter.storage.FilterStateStorage
import ru.practicum.android.diploma.data.network.HHApi
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient

private const val BASE_URL_HH = "https://api.hh.ru/"

val dataModule = module {
    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<HHApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL_HH)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // Передаем кастомный клиент. После тестов убрать
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

    single {
        androidContext()
            .getSharedPreferences("MEGAHH_PREFERENCES", Context.MODE_PRIVATE)
    }

    single { Gson() }

    single<FilterStateStorage> {
        FilterStateStorageImpl(
            get(),
            get()
        )
    }
}
