package org.opensource.anivibe.anime.SearchedAnime


import com.google.gson.annotations.SerializedName
import org.opensource.anivibe.anime.Result

data class SearchedAnime(
    @SerializedName("data")
    val `data`: List<Result>,
)