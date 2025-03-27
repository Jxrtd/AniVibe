package org.opensource.anivibe.anime.TopAnime


import com.google.gson.annotations.SerializedName
import org.opensource.anivibe.anime.Result

data class TopAnime(
    @SerializedName("data")
    val `data`: List<Result>,
    @SerializedName("pagination")
    val pagination: Pagination
)