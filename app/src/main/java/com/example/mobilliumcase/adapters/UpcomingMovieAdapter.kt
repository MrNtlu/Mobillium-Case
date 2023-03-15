package com.example.mobilliumcase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilliumcase.adapters.viewholders.*
import com.example.mobilliumcase.databinding.*
import com.example.mobilliumcase.interfaces.Interaction
import com.example.mobilliumcase.models.MovieModel
import com.example.mobilliumcase.utils.RecyclerViewEnum

class UpcomingMovieAdapter(
    override val interaction: Interaction<MovieModel>,
): BaseAdapter<MovieModel>(interaction) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            RecyclerViewEnum.Loading.value -> LoadingViewHolder(CellLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            RecyclerViewEnum.PaginationLoading.value -> PaginationLoadingViewHolder(CellPaginationLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            RecyclerViewEnum.PaginationExhaust.value -> PaginationExhaustViewHolder(CellPaginationExhaustBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            RecyclerViewEnum.Error.value -> ErrorItemViewHolder(CellErrorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> ItemViewHolder(CellItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun handleDiffUtil(newList: ArrayList<MovieModel>) {
        val diffUtil = MovieDiffUtilCallBack(
            arrayList,
            newList
        )

        val diffResults = DiffUtil.calculateDiff(diffUtil, true)

        arrayList = newList.toList() as ArrayList<MovieModel>
        diffResults.dispatchUpdatesTo(this)
    }
}