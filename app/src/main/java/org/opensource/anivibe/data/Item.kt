package org.opensource.anivibe.data

data class Item(
    val id: String? = null,
    var profileImagePath: String?,
    var username: String = "",
    var description: String = "",
    var isLiked: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf(),
    var timestamp: Long = System.currentTimeMillis()
) {
}