package com.example.mobilliumcase.viewmodels

import androidx.lifecycle.*
import com.example.mobilliumcase.models.MovieModel
import com.example.mobilliumcase.models.MovieResponse
import com.example.mobilliumcase.repository.MainRepository
import com.example.mobilliumcase.utils.NetworkListResponse
import com.example.mobilliumcase.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val PAGE_KEY = "rv.upcoming.page"
const val SCROLL_POSITION_KEY = "rv.upcoming.scroll_position"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, //For process death
    private val repository: MainRepository,
): ViewModel() {
    private val _upcomingList = MutableLiveData<NetworkListResponse<List<MovieModel>>>()
    val upcomingMovies: LiveData<NetworkListResponse<List<MovieModel>>> = _upcomingList

    private val _nowPlayingMovies = MutableLiveData<NetworkResponse<MovieResponse>>()
    val nowPlayingMovies: LiveData<NetworkResponse<MovieResponse>> = _nowPlayingMovies

    // Process Death variables
    var isRestoringData = false
    private var page: Int = savedStateHandle[PAGE_KEY] ?: 1
    var scrollPosition: Int = savedStateHandle[SCROLL_POSITION_KEY] ?: 0
        private set

    // Variable for detecting orientation change
    var didOrientationChange = false

    init {
        if (page != 1) {
            restoreData()
        } else {
            fetchUpcomingMovies()
            fetchNowPlayingMovies()
        }
    }

    fun refreshData() {
        setPagePosition(1)
        fetchUpcomingMovies()
    }

    fun fetchUpcomingMovies() = viewModelScope.launch(Dispatchers.IO) {
        val prevList = arrayListOf<MovieModel>()
        if (_upcomingList.value is NetworkListResponse.Success) {
            prevList.addAll((_upcomingList.value as NetworkListResponse.Success<List<MovieModel>>).data.toCollection(ArrayList()))
        }

        repository.fetchUpcomingMovies(page).collect { response ->
            withContext(Dispatchers.Main) {
                if (response is NetworkListResponse.Success) {
                    prevList.addAll(response.data)

                    _upcomingList.value = NetworkListResponse.Success(
                        prevList,
                        isPaginationData = response.isPaginationData,
                        isPaginationExhausted = response.isPaginationExhausted,
                    )

                    setPagePosition(page.plus(1))
                } else {
                    _upcomingList.value = response
                }
            }
        }
    }

    fun fetchNowPlayingMovies() = viewModelScope.launch(Dispatchers.IO) {
        repository.fetchNowPlayingMovies().collect {response ->
            withContext(Dispatchers.Main) {
                _nowPlayingMovies.value = response
            }
        }
    }

    /**
     * This function is used to restore data after the Process Death
     *
     * This is not really the ideal for production.
     * Normally we need to do caching and get retrieve the data from there.
     * Since we don't have it in this case, I'll simply make requests.
     */
    private fun restoreData() {
        isRestoringData = true

        var isPaginationExhausted = false
        val tempList = arrayListOf<MovieModel>()
        viewModelScope.launch(Dispatchers.IO) {
            for (p in 1..page) {
                val job = launch(Dispatchers.IO) {
                    repository.fetchUpcomingMovies(p).collect { response ->
                        if (response is NetworkListResponse.Success) {
                            tempList.addAll(response.data)
                            isPaginationExhausted = response.isPaginationExhausted
                        }
                    }
                }
                job.join()
            }
            withContext(Dispatchers.Main) {
                _upcomingList.value = NetworkListResponse.Success(
                    tempList,
                    isPaginationData = true,
                    isPaginationExhausted = isPaginationExhausted,
                )
            }
        }

        fetchNowPlayingMovies()
    }

    private fun setPagePosition(newPage: Int) {
        page = newPage
        savedStateHandle[PAGE_KEY] = newPage
    }

    fun setScrollPosition(newPosition: Int) {
        if (!isRestoringData && !didOrientationChange) {
            scrollPosition = newPosition
            savedStateHandle[SCROLL_POSITION_KEY] = newPosition
        }
    }
}