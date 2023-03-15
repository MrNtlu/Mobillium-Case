package com.example.mobilliumcase.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.example.mobilliumcase.databinding.CellPaginationExhaustBinding
import com.example.mobilliumcase.interfaces.Interaction
import com.example.mobilliumcase.models.MovieModel

class PaginationExhaustViewHolder(
    private val binding: CellPaginationExhaustBinding,
): RecyclerView.ViewHolder(binding.root), PaginationExhaustViewHolderBind<MovieModel> {
    override fun bind(interaction: Interaction<MovieModel>) {
        binding.topButton.setOnClickListener { interaction.onExhaustButtonPressed() }
    }
}