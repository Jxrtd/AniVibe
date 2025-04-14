package org.opensource.anivibe.anime


import com.google.gson.annotations.SerializedName
import org.opensource.anivibe.anime.TopAnime.Images

data class Result(
    @SerializedName("airing")
    val airing: Boolean,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("episodes")
    val episodes: Int,
    @SerializedName("images")
    val imageUrl: Images,
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("members")
    val members: Int,
    @SerializedName("rated")
    val rated: String,
    @SerializedName("score")
    val score: Double,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("synopsis")
    val synopsis: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String,
)
data class SavedAnime(
    @SerializedName("data")
    val data: List<Result>
)
