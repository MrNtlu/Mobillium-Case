package com.example.mobilliumcase.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.mobilliumcase.models.MovieModel

class MovieDiffUtilCallBack(
    private val oldList: List<MovieModel>,
    private val newList: List<MovieModel>,
): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].titleOriginal != newList[newItemPosition].titleOriginal -> false
            else -> true
        }
    }
}