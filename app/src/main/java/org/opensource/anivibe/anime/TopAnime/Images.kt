package org.opensource.anivibe.anime.TopAnime


import com.google.gson.annotations.SerializedName

data class Images(
    @SerializedName("jpg")
    val jpg: ImagesURL,
    @SerializedName("webp")
    val webp: Webp
)

data class ImagesURL(
    @SerializedName("image_url")
    val imagesUrl: String,
) {

}