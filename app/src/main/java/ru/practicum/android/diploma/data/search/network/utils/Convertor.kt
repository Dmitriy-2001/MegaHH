package ru.practicum.android.diploma.data.search.network.utils

import ru.practicum.android.diploma.data.search.dto.Salary
import ru.practicum.android.diploma.data.search.dto.response.GetVacancyDetailsResponse
import ru.practicum.android.diploma.data.search.dto.response.SearchVacanciesResponse
import ru.practicum.android.diploma.domain.filter.models.FilterParams
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.domain.search.models.VacancyModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Convertor {
    private const val hhLink = "https://hh.ru/vacancy"

    fun SearchVacanciesResponse.convertToModel() = VacanciesModel(
        pages = this.pages,
        itemsCount = this.found,
        currentPage = this.page,
        items = this.items.map {
            VacancyModel(
                id = it.id,
                name = it.name,
                employer = it.employer.name,
                logoUrl = it.employer.logoUrls?.url240,
                city = it.area.name,
                salary = getSalaryString(it.salary),
                description = it.description ?: "",
                employmentForm = it.employment?.name,
                experience = it.experience.name,
                keySkills = it.keySkills?.map { it.name } ?: listOf(),
                alternateUrl = it.employer.alternateUrl ?: "$hhLink/${it.id}",
                workFormat = it.workFormat?.joinToString(",") { format -> format.name } ?: ""
            )
        }
    )

    fun GetVacancyDetailsResponse.convertToModel() = VacancyModel(
        id = this.id,
        name = this.name,
        employer = this.employer.name,
        logoUrl = this.employer.logoUrls?.url240,
        city = this.area.name,
        salary = getSalaryString(this.salary),
        description = this.description,
        employmentForm = this.employment?.name,
        experience = this.experience.name,
        keySkills = this.keySkills.map { it.name },
        alternateUrl = this.employer.alternateUrl ?: "$hhLink/${this.id}",
        workFormat = this.workFormat?.joinToString(",") { format -> format.name } ?: ""
    )

    fun FilterParams.convertToMap() = mapOf(
        "area" to (this.area?.id ?: this.country?.id),
        "industry" to this.industry?.id,
        "salary" to this.salary,
        "only_with_salary" to this.doNotShowWithoutSalary
    ).filterValues { it != null } as Map<String, String>

    private fun getSalaryString(salary: Salary?) = buildString {
        salary?.from?.let { append("от ${formatSalary(it)} ") }
        salary?.to?.let { append("до ${formatSalary(it)}") }
        if (isEmpty()) append("Зарплата не указана") else append(" ${salary?.currency?.symbol}")
    }

    private fun formatSalary(salary: Int): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = ' ' // Используем пробел вместо запятой, по дефолту запятая
        }
        val formatter = DecimalFormat("#,###", symbols)
        return formatter.format(salary)
    }
}
