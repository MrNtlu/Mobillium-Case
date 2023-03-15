package com.example.mobilliumcase.repository

import com.example.mobilliumcase.models.MovieModel
import com.example.mobilliumcase.service.MovieApiService
import com.example.mobilliumcase.utils.NetworkListResponse
import com.example.mobilliumcase.utils.requestFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val movieApiService: MovieApiService
) {

    fun fetchUpcomingMovies(page: Int): Flow<NetworkListResponse<List<MovieModel>>> = flow {
        val isPaginating = page != 1

        emit(NetworkListResponse.Loading(isPaginating))

        try {
            val response = movieApiService.getUpcomingMovies(page)

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val isPaginationExhausted = page >= responseBody.totalPages

                emit(NetworkListResponse.Success(
                    responseBody.results,
                    isPaginationData = isPaginating,
                    isPaginationExhausted = isPaginationExhausted,
                ))
            } else {
                emit(handlePaginationError<MovieModel>(isPaginating, response.message()))
            }
        } catch (e: Exception) {
            emit(handlePaginationError<MovieModel>(isPaginating, e.message ?: e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchNowPlayingMovies() = requestFlow {
        movieApiService.getNowPlayingMovies()
    }

    private fun<T> handlePaginationError(isPaginating: Boolean, errorMessage: String): NetworkListResponse<List<T>> {
        return if (isPaginating) {
            NetworkListResponse.Success(
                listOf<T>(),
                isPaginationData = true,
                isPaginationExhausted = true
            )
        } else {
            NetworkListResponse.Failure(errorMessage)
        }
    }
}