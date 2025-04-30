// In Comment.kt
package org.opensource.anivibe.data

data class Comment(
    var username: String,
    val content: String,
    var profileImagePath: String?,
    val userId: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(userId.isNotBlank()) { "User ID cannot be blank" }
    }
}