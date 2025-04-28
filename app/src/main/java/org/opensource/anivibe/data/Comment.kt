// In Comment.kt
package org.opensource.anivibe.data

data class Comment(
    var username: String,
    var content: String,
    var profileImagePath: String? = null,
    // Add userId to link comment to user
    var userId: String? = null
)