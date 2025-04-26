package org.opensource.anivibe.data

data class Item(
    val id: String?,
    var profileImagePath: String?,
    var username: String,
    val description: String,
    var isLiked: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf()
)
data class Comment(
    val username: String,
    val content: String,
    val profileImagePath: String? = null
)