package ru.practicum.android.diploma.data.search.dto.response

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.data.search.dto.Address
import ru.practicum.android.diploma.data.search.dto.Area
import ru.practicum.android.diploma.data.search.dto.Employer
import ru.practicum.android.diploma.data.search.dto.EmploymentForm
import ru.practicum.android.diploma.data.search.dto.Experience
import ru.practicum.android.diploma.data.search.dto.KeySkill
import ru.practicum.android.diploma.data.search.dto.Salary

data class GetVacancyDetailsResponse(
    val id: String,
    val name: String,
    val employer: Employer,
    val address: Address?,
    val salary: Salary?,
    val description: String,
    val employmentForm: EmploymentForm?,
    val experience: Experience,
    @SerializedName("key_skills")
    val keySkills: List<KeySkill> = listOf(),
    val area: Area
)
