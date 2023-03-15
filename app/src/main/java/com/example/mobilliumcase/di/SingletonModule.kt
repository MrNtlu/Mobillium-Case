package com.example.mobilliumcase.di

import com.example.mobilliumcase.service.APIInterceptor
import com.example.mobilliumcase.service.MovieApiService
import com.example.mobilliumcase.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        interceptor: APIInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideAPIInterceptor(): APIInterceptor = APIInterceptor()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(Constants.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideMovieAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): MovieApiService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(MovieApiService::class.java)
}