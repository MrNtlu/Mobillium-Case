package com.example.mobilliumcase.service

import com.example.mobilliumcase.models.MovieDetailsModel
import com.example.mobilliumcase.models.MovieModel
import com.example.mobilliumcase.models.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/now_playing?page=1")
    suspend fun getNowPlayingMovies(): Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int
    ): Response<MovieResponse>

    // Note: This could be separated but since we don't have a lot of endpoints
    // I've decided to put them into single file.
    @GET("movie/")
    suspend fun getMovieDetails(
        @Query("page") page: Int
    ): Response<MovieDetailsModel>
}