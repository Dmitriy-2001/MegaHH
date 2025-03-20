package ru.practicum.android.diploma.data.search.network.utils

import ru.practicum.android.diploma.data.search.dto.Salary
import ru.practicum.android.diploma.data.search.dto.response.SearchVacanciesResponse
import ru.practicum.android.diploma.domain.search.models.VacanciesModel
import ru.practicum.android.diploma.domain.search.models.VacancyModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Convertor {
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
                city = it.address.city,
                salary = getSalaryString(it.salary),
                currency = it.salary.currency?.symbol
            )
        }
    )

    private fun getSalaryString(salary: Salary) = buildString {
        salary.from?.let { append("от ${formatSalary(it)} ") }
        salary.to?.let { append("до ${formatSalary(it)}") }
        if (isEmpty()) append("Зарплата не указана")
    }

    private fun formatSalary(salary: Int): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = ' ' // Используем пробел вместо запятой, по дефолту запятая
        }
        val formatter = DecimalFormat("#,###", symbols)
        return formatter.format(salary)
    }
}
