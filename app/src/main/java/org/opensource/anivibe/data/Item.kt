package org.opensource.anivibe.data

data class Item(
    val id: String? = null,
    var profileImagePath: String?,    // Change to var to allow updates
    var username: String = "",
    var description: String = "",
    var isLiked: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf(),  // Use your own Comment class
    var timestamp: Long = System.currentTimeMillis()  // Added timestamp field
) {
}