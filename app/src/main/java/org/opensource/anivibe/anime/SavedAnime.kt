package org.opensource.anivibe.anime

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedAnime(
    val data: List<Result> = emptyList()
) : Parcelable