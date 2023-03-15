package com.example.mobilliumcase.models

import com.google.gson.annotations.SerializedName

data class MovieDetailsModel(
    val id: Int,
    @SerializedName("imdb_id")
    val imdbID: String,
    @SerializedName("original_title")
    val titleOriginal: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path")
    val poster: String,
    @SerializedName("release_date")
    val releaseDate: String,
    val runtime: Int,
    val status: String,
    val tagline: String,
    val title: String,
    @SerializedName("vote_average")
    val voteAvg: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("backdrop_path")
    val backdropPath: String,
)