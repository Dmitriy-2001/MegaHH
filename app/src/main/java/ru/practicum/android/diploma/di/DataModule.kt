package ru.practicum.android.diploma.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.favorites.db.AppDatabase

val dataModule = module {

    single {
        Room.databaseBuilder(
            androidContext(), AppDatabase::class.java, "database.megaHH"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().favoriteVacancyDao() }
}
