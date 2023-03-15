package com.example.mobilliumcase.di

import com.example.mobilliumcase.repository.MovieDetailsRepository
import com.example.mobilliumcase.repository.MainRepository
import com.example.mobilliumcase.service.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {

    @Provides
    fun provideMainRepository(movieApiService: MovieApiService) = MainRepository(movieApiService)

    @Provides
    fun provideDetailsRepository(movieApiService: MovieApiService) = MovieDetailsRepository(movieApiService)
}