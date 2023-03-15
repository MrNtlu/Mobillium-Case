package com.example.mobilliumcase.repository

import com.example.mobilliumcase.service.MovieApiService
import javax.inject.Inject

class DetailsRepository @Inject constructor(
    private val movieApiService: MovieApiService,
){

}