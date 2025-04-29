// In Item.kt
package org.opensource.anivibe.data

data class Item(
    val id: String? = null,
    var profileImagePath: String? = null,    // Change to var to allow updates
    var username: String = "",
    var description: String = "",
    var isLiked: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf(),  // Use your own Comment class
    var timestamp: Long = System.currentTimeMillis()  // Added timestamp field
) {
    // Add a debug method to print item details
    fun logDetails(tag: String) {
        android.util.Log.d(tag, "Item[$id] - username: $username")
        android.util.Log.d(tag, "Item[$id] - description: ${description.take(30)}...")
        android.util.Log.d(tag, "Item[$id] - profileImagePath: $profileImagePath")
        android.util.Log.d(tag, "Item[$id] - isLiked: $isLiked")
        android.util.Log.d(tag, "Item[$id] - comments: ${comments.size}")
    }
}