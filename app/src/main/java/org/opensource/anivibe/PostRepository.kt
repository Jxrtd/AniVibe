package org.opensource.anivibe.repository

import android.content.Context
import org.opensource.anivibe.data.Comment
import org.opensource.anivibe.data.Item
import org.json.JSONArray
import org.json.JSONObject

object PostRepository {
    private var posts = mutableListOf<Item>()

    fun getPost(postId: String): Item? {
        return posts.find { it.id == postId }
    }

    fun addPost(context: Context, post: Item) {
        posts.add(0, post)
        savePosts(context)
    }

    fun getPosts(context: Context): List<Item> {
        if (posts.isEmpty()) loadPosts(context)
        return posts
    }


    fun deletePost(context: Context, postId: String) {
        val postToRemove = posts.find { it.id == postId }
        if (postToRemove != null) {
            posts.remove(postToRemove)
            savePosts(context)
        }
    }

    // Keep the old method for backward compatibility (or remove it)
    @Deprecated("Use deletePost with postId instead")
    fun deletePostByPosition(context: Context, position: String) {
        val positionInt = position.toIntOrNull()
        if (positionInt != null && positionInt in posts.indices) {
            posts.removeAt(positionInt)
            savePosts(context)
        }
    }

    fun addComment(postId: String, comment: Comment) {
        val post = posts.find { it.id == postId }
        post?.comments?.add(comment)
    }

    fun toggleLike(postId: String?) {
        val post = posts.find { it.id == postId }
        post?.let {
            it.isLiked = !it.isLiked
        }
    }

    private fun savePosts(context: Context) {
        val prefs = context.getSharedPreferences("Posts", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()

        posts.forEach { item ->
            val itemJson = JSONObject().apply {
                put("id", item.id)
                put("username", item.username)
                put("profileImagePath", item.profileImagePath)
                put("description", item.description)
                put("isLiked", item.isLiked)

                val commentsJson = JSONArray()
                item.comments.forEach { comment ->
                    val commentJson = JSONObject().apply {
                        put("username", comment.username)
                        put("content", comment.content)
                        put("profileImagePath", comment.profileImagePath)
                    }
                    commentsJson.put(commentJson)
                }
                put("comments", commentsJson)
            }
            jsonArray.put(itemJson)
        }

        prefs.edit()
            .putString("postList", jsonArray.toString())
            .apply()
    }

    private fun loadPosts(context: Context) {
        val prefs = context.getSharedPreferences("Posts", Context.MODE_PRIVATE)
        val serialized = prefs.getString("postList", "") ?: ""

        posts.clear()

        if (serialized.isNotEmpty()) {
            try {
                val jsonArray = JSONArray(serialized)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    val comments = mutableListOf<Comment>()
                    val commentsArray = obj.optJSONArray("comments") ?: JSONArray()
                    for (j in 0 until commentsArray.length()) {
                        val commentObj = commentsArray.getJSONObject(j)
                        comments.add(
                            Comment(
                                username = commentObj.getString("username"),
                                content = commentObj.getString("content"),
                                profileImagePath = commentObj.optString("profileImagePath", null)
                            )
                        )
                    }

                    posts.add(
                        Item(
                            id = obj.getString("id"),
                            profileImagePath = obj.optString("profileImagePath", null),
                            username = obj.getString("username"),
                            description = obj.getString("description"),
                            isLiked = obj.getBoolean("isLiked"),
                            comments = comments
                        )
                    )
                }

            } catch (e: Exception) {
                // Clear old incompatible data
                prefs.edit().remove("postList").apply()
            }
        }
    }

    fun updateUserInfo(context: Context, oldUsername: String, newUsername: String, newProfileImagePath: String?) {
        var updated = false

        android.util.Log.d("PostRepository", "Updating user info: $oldUsername -> $newUsername")
        android.util.Log.d("PostRepository", "New profile image path: $newProfileImagePath")
        android.util.Log.d("PostRepository", "Total posts before update: ${posts.size}")

        posts.forEach { item ->
            // Check if this post belongs to the user being updated
            if (item.username == oldUsername) {
                android.util.Log.d("PostRepository", "Found post by user: ${item.id}")
                android.util.Log.d("PostRepository", "  Old username: ${item.username}")
                android.util.Log.d("PostRepository", "  Old profile image: ${item.profileImagePath}")

                // Update username
                item.username = newUsername

                // Update profile image path if provided
                if (newProfileImagePath != null) {
                    item.profileImagePath = newProfileImagePath
                }

                android.util.Log.d("PostRepository", "  Updated username: ${item.username}")
                android.util.Log.d("PostRepository", "  Updated profile image: ${item.profileImagePath}")

                updated = true
            }

            // Also update usernames and profile images in comments
            item.comments.forEachIndexed { index, comment ->
                if (comment.username == oldUsername) {
                    android.util.Log.d("PostRepository", "Found comment by user in post ${item.id}, comment #$index")
                    android.util.Log.d("PostRepository", "  Old username: ${comment.username}")
                    android.util.Log.d("PostRepository", "  Old profile image: ${comment.profileImagePath}")

                    // Update username in comment
                    comment.username = newUsername

                    // Update profile image in comment
                    if (newProfileImagePath != null) {
                        comment.profileImagePath = newProfileImagePath
                    }

                    android.util.Log.d("PostRepository", "  Updated username: ${comment.username}")
                    android.util.Log.d("PostRepository", "  Updated profile image: ${comment.profileImagePath}")

                    updated = true
                }
            }
        }

        android.util.Log.d("PostRepository", "Changes made: $updated")

        if (updated) {
            android.util.Log.d("PostRepository", "Saving updated posts to SharedPreferences")
            savePosts(context)
        }
    }

    fun updateUsername(context: Context, oldUsername: String, newUsername: String) {
        var updated = false
        posts.forEach { item ->
            if (item.username == oldUsername) {
                item.username = newUsername
                updated = true
            }
        }

        if (updated) {
            savePosts(context)
        }
    }
}
