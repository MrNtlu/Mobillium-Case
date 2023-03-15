package com.example.mobilliumcase.repository

import com.example.mobilliumcase.service.MovieApiService
import com.example.mobilliumcase.utils.requestFlow
import javax.inject.Inject

class MovieDetailsRepository @Inject constructor(
    private val movieApiService: MovieApiService,
){

    fun fetchMovieDetails(movieID: Int) = requestFlow {
        movieApiService.getMovieDetails(movieID)
    }
}