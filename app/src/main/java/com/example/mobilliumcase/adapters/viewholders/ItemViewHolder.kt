package com.example.mobilliumcase.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.example.mobilliumcase.R
import com.example.mobilliumcase.databinding.CellItemBinding
import com.example.mobilliumcase.interfaces.Interaction
import com.example.mobilliumcase.models.MovieModel
import com.example.mobilliumcase.utils.Constants
import com.example.mobilliumcase.utils.loadWithGlide
import com.example.mobilliumcase.utils.setGone
import com.example.mobilliumcase.utils.setVisible

class ItemViewHolder(
    private val binding: CellItemBinding,
): RecyclerView.ViewHolder(binding.root), ItemViewHolderBind<MovieModel> {
    override fun bind(item: MovieModel, position: Int, interaction: Interaction<MovieModel>) {

        if (item.backdropPath == null) {
            binding.movieImageProgress.setGone()
            binding.movieImage.setImageResource(R.drawable.ic_error_48)
        } else {
            binding.movieImageProgress.setVisible()
            binding.movieImage.loadWithGlide(
                Constants.TMDB_SMALL_IMAGE_URL.plus(item.backdropPath),
                binding.movieImageProgress
            )
        }

        binding.movieTitleTV.text = item.title
        binding.movieDescriptionTV.text = item.overview
        binding.movieDateTV.text = item.releaseDate

        binding.root.setOnClickListener {
            interaction.onItemSelected(item, position)
        }
    }
}