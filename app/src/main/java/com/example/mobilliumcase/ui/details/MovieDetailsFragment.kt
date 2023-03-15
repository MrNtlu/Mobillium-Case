package com.example.mobilliumcase.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mobilliumcase.databinding.FragmentMovieDetailsBinding
import com.example.mobilliumcase.ui.BaseFragment
import com.example.mobilliumcase.utils.*
import com.example.mobilliumcase.viewmodels.MovieDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class MovieDetailsFragment : BaseFragment<FragmentMovieDetailsBinding>() {

    private var movieID by Delegates.notNull<Int>()
    private lateinit var imdbID: String
    private val viewModel: MovieDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            movieID = requireArguments().getInt("id")
        } else {
            showToast(context, "Invalid ID.")
            navController.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMovieDetails(movieID)

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.imdbButton.setOnClickListener {
            if (::imdbID.isInitialized) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.IMDB_REDIRECT_URL.plus(imdbID)))
                startActivity(intent)
            } else {
                showToast(context, "Please wait.")
            }
        }
    }

    private fun setObservers() {
        viewModel.movieDetails.observe(viewLifecycleOwner) { response ->
            when(response) {
                is NetworkResponse.Failure -> {
                    binding.loadingInc.root.setGone()
                    binding.errorInc.root.setVisible()

                    binding.errorInc.apply {
                        errorText.text = response.errorMessage
                        refreshButton.setOnClickListener {
                            viewModel.getMovieDetails(movieID)
                        }
                    }
                }
                NetworkResponse.Loading -> {
                    binding.loadingInc.root.setVisible()
                    binding.errorInc.root.setGone()
                }
                is NetworkResponse.Success -> {
                    binding.loadingInc.root.setGone()
                    binding.errorInc.root.setGone()

                    response.data.apply {
                        this@MovieDetailsFragment.imdbID = imdbID

                        if (backdropPath != null) {
                            binding.movieDetailsCollapsingToolbar.setVisible()
                            binding.movieDetailsImage.loadWithGlide(
                                Constants.TMDB_IMAGE_URL.plus(backdropPath),
                                binding.movieDetailsProgressbar
                            )
                        } else {
                            binding.movieDetailsCollapsingToolbar.setGone()
                        }
                        binding.rateTV.text = String.format("%.1f", voteAvg)
                        binding.dateTV.text = releaseDate.convertToFormattedDate()
                        binding.titleTV.text = title
                        binding.movieDetailsTV.text = overview
                    }
                }
            }
        }
    }
}