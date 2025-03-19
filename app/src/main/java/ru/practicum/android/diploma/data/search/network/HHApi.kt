package ru.practicum.android.diploma.data.search.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.search.dto.response.SearchVacanciesResponse

interface HHApi {
    @Headers("Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}", "HH-User-Agent: megaHH (bizim734@gmail.com)")
    @GET("/vacancies")
    suspend fun searchVacancy(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("text") text: String
    ): SearchVacanciesResponse
}
