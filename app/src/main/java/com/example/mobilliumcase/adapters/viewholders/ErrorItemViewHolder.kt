package com.example.mobilliumcase.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.example.mobilliumcase.databinding.CellErrorBinding
import com.example.mobilliumcase.interfaces.Interaction
import com.example.mobilliumcase.models.MovieDetailsModel

class ErrorItemViewHolder(
    private val binding: CellErrorBinding,
): RecyclerView.ViewHolder(binding.root), ErrorViewHolderBind<MovieDetailsModel> {
    override fun bind(errorMessage: String?, interaction: Interaction<MovieDetailsModel>) {
        binding.errorText.text = errorMessage

        binding.refreshButton.setOnClickListener { interaction.onErrorRefreshPressed() }
    }
}