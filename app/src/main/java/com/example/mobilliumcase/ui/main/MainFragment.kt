package com.example.mobilliumcase.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mobilliumcase.R
import com.example.mobilliumcase.adapters.UpcomingMovieAdapter
import com.example.mobilliumcase.databinding.FragmentMainBinding
import com.example.mobilliumcase.interfaces.Interaction
import com.example.mobilliumcase.models.MovieModel
import com.example.mobilliumcase.ui.BaseFragment
import com.example.mobilliumcase.utils.*
import com.example.mobilliumcase.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private var movieAdapter: UpcomingMovieAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setRecyclerView()
        setObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.didOrientationChange = true
    }

    private fun setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()

            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setRecyclerView() {
        binding.upcomingRV.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            movieAdapter = UpcomingMovieAdapter(object: Interaction<MovieModel> {
                override fun onItemSelected(item: MovieModel, position: Int) {
                    navigateToDetails(item.id)
                }

                override fun onErrorRefreshPressed() {
                    viewModel.refreshData()
                }

                override fun onExhaustButtonPressed() {
                    viewLifecycleOwner.lifecycleScope.launch {
                        quickScrollToTop()
                    }
                }
            })
            adapter = movieAdapter

            var isScrolling = false
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    isScrolling = newState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val itemCount = linearLayoutManager.itemCount
                    val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()

                    val centerScrollPosition = (linearLayoutManager.findLastCompletelyVisibleItemPosition() + linearLayoutManager.findFirstCompletelyVisibleItemPosition()) / 2
                    viewModel.setScrollPosition(centerScrollPosition)

                    movieAdapter?.let {
                        if (
                            isScrolling &&
                            lastVisibleItemPosition >= itemCount.minus(5) &&
                            it.canPaginate &&
                            !it.isPaginating
                        ) {
                            viewModel.fetchUpcomingMovies()
                        }
                    }
                }
            })
        }
    }

    private fun setObservers() {
        binding.appbarLayout.setExpanded(viewModel.scrollPosition < 5)

        viewModel.nowPlayingMovies.observe(viewLifecycleOwner) { response ->
            when(response) {
                is NetworkResponse.Failure -> {
                    binding.imageSlider.setGone()
                    binding.errorInc.root.setVisible()
                    binding.loadingInc.root.setGone()

                    binding.errorInc.apply {
                        animationView.setGone()
                        errorText.text = response.errorMessage
                        refreshButton.setOnClickListener {
                            viewModel.fetchNowPlayingMovies()
                        }
                    }
                }
                NetworkResponse.Loading -> {
                    binding.imageSlider.setGone()
                    binding.errorInc.root.setGone()
                    binding.loadingInc.root.setVisible()
                }
                is NetworkResponse.Success -> {
                    binding.imageSlider.setVisible()
                    binding.errorInc.root.setGone()
                    binding.loadingInc.root.setGone()

                    val validMovies = response.data.results.filter { it.backdropPath != null }

                    val sliderIDList = validMovies.map { it.id }
                    val imageSliderList = validMovies.map { SlideModel(Constants.TMDB_IMAGE_URL.plus(it.backdropPath), it.title, ScaleTypes.CENTER_CROP) }
                    binding.imageSlider.setImageList(imageSliderList)

                    // Has to be called here otherwise doesn't work.
                    binding.imageSlider.setItemClickListener(object: ItemClickListener {
                        override fun onItemSelected(position: Int) {
                            navigateToDetails(sliderIDList[position])
                        }
                    })
                }
            }
        }

        viewModel.upcomingMovies.observe(viewLifecycleOwner) { response ->
            binding.swipeRefreshLayout.isEnabled = when (response) {
                is NetworkListResponse.Success -> {
                    true
                }
                is NetworkListResponse.Failure -> {
                    false
                }
                else -> false
            }

            when(response) {
                is NetworkListResponse.Failure -> {
                    movieAdapter?.setErrorView(response.errorMessage)
                }
                is NetworkListResponse.Loading -> {
                    movieAdapter?.setLoadingView(response.isPaginating)
                }
                is NetworkListResponse.Success -> {
                    movieAdapter?.setData(response.data.toCollection(ArrayList()), response.isPaginationData, response.isPaginationExhausted)

                    if (viewModel.isRestoringData || viewModel.didOrientationChange) {
                        binding.upcomingRV.scrollToPosition(viewModel.scrollPosition - 1)

                        if (viewModel.isRestoringData) {
                            viewModel.isRestoringData = false
                        } else {
                            viewModel.didOrientationChange = false
                        }
                    } else {
                        binding.upcomingRV.scrollToPosition(viewModel.scrollPosition - 1)
                    }
                }
            }
        }
    }

    private fun navigateToDetails(id: Int) {
        val bundle = bundleOf(
            "id" to id
        )
        navController.navigate(R.id.action_mainFragment_to_movieDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        movieAdapter = null
        super.onDestroyView()
    }
}