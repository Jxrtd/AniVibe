package org.opensource.anivibe.anime

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(
    @SerializedName("mal_id")
    val malId: Int?,
    val title: String?,
    val synopsis: String?,
    val score: Double?,
    val type: String?,
    @SerializedName("images")
    val imageUrl: ImageUrl?
) : Parcelable

@Parcelize
data class ImageUrl(
    val jpg: JpgImage
) : Parcelable

@Parcelize
data class JpgImage(
    @SerializedName("image_url")
    val imagesUrl: String?
) : Parcelable