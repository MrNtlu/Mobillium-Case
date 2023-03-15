package com.example.mobilliumcase.models

import com.google.gson.annotations.SerializedName

data class MovieModel(
    val id: Int,
    @SerializedName("original_title")
    val titleOriginal: String,
    val overview: String,
    @SerializedName("poster_path")
    val poster: String,
    @SerializedName("release_date")
    val releaseDate: String,
    val title: String,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
)