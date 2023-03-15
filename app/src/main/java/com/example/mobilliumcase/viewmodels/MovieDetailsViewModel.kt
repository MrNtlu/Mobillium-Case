package com.example.mobilliumcase.viewmodels

import androidx.lifecycle.*
import com.example.mobilliumcase.models.MovieDetailsModel
import com.example.mobilliumcase.repository.MovieDetailsRepository
import com.example.mobilliumcase.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val MOVIE_ID = "movie_id"

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, //For process death
    private val repository: MovieDetailsRepository,
): ViewModel() {

    private val _movieDetails = MutableLiveData<NetworkResponse<MovieDetailsModel>>()
    val movieDetails: LiveData<NetworkResponse<MovieDetailsModel>> = _movieDetails

    private var movieID = savedStateHandle[MOVIE_ID] ?: -1

    init {
        if (movieID != -1) {
            getMovieDetails(movieID)
        }
    }

    fun getMovieDetails(movieID: Int) = viewModelScope.launch(Dispatchers.IO) {
        savedStateHandle[MOVIE_ID] = movieID

        repository.fetchMovieDetails(movieID).collect { response ->
            withContext(Dispatchers.Main) {
                _movieDetails.value = response
            }
        }
    }
}