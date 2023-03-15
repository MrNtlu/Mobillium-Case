package com.example.mobilliumcase.utils

sealed class NetworkResponse<out T> {
    object Loading: NetworkResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ): NetworkResponse<T>()

    data class Failure(
        val errorMessage: String,
    ): NetworkResponse<Nothing>()
}