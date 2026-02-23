package me.thankgodr.fintechchallegeapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    val success: Boolean,
    val data: T? = null,
    val errors: List<String>? = null
)
