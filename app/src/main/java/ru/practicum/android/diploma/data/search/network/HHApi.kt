package ru.practicum.android.diploma.data.search.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.search.dto.response.GetVacancyDetailsResponse
import ru.practicum.android.diploma.data.search.dto.response.SearchVacanciesResponse

interface HHApi {
    @Headers(authHeader, userAgentHeader)
    @GET("/vacancies")
    suspend fun searchVacancy(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("text") text: String
    ): SearchVacanciesResponse

    @Headers(authHeader, userAgentHeader)
    @GET("/vacancies/{vacancy_id}")
    suspend fun getVacancyDetails(
        @Path("vacancy_id") vacancyId: String
    ): GetVacancyDetailsResponse

    companion object {
        private const val authHeader = "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}"
        private const val userAgentHeader = "HH-User-Agent: megaHH (bizim734@gmail.com)"
    }
}
