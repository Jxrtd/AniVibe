package org.opensource.anivibe

import android.content.Context
import android.util.Log
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

    @Deprecated("Use deletePost with postId instead")
    fun deletePostByPosition(context: Context, position: String) {
        val positionInt = position.toIntOrNull()
        if (positionInt != null && positionInt in posts.indices) {
            posts.removeAt(positionInt)
            savePosts(context)
        }
    }

    fun addComment(context: Context, postId: String, comment: Comment) {
        val post = posts.find { it.id == postId }
        post?.comments?.add(comment)
        savePosts(context)
    }

    fun deleteComment(context: Context, postId: String, comment: Comment) {
        val post = posts.find { it.id == postId }
        post?.comments?.remove(comment)
        savePosts(context)
    }

    fun toggleLike(postId: String?) {
        val post = posts.find { it.id == postId }
        post?.let {
            it.isLiked = !it.isLiked
        }
    }

    fun savePosts(context: Context, updatedPosts: List<Item>? = null) {
        if (updatedPosts != null) {
            posts.clear()
            posts.addAll(updatedPosts)
        }

        val prefs = context.getSharedPreferences("Posts", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()

        posts.forEach { item ->
            val itemJson = JSONObject().apply {
                put("id", item.id)
                put("username", item.username)
                put("profileImagePath", item.profileImagePath)
                put("description", item.description)
                put("isLiked", item.isLiked)
                put("timestamp", item.timestamp)

                val commentsJson = JSONArray()
                item.comments.forEach { comment ->
                    val commentJson = JSONObject().apply {
                        put("username", comment.username)
                        put("content", comment.content)
                        put("profileImagePath", comment.profileImagePath)
                        put("userId", comment.userId)
                        put("timestamp", comment.timestamp)
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
                                profileImagePath = commentObj.optString("profileImagePath", null),
                                userId = commentObj.optString("userId", commentObj.getString("username")), // Fallback to username if userId not present
                                timestamp = commentObj.optLong("timestamp", System.currentTimeMillis()) // Default to current time if missing
                            )
                        )
                    }

                    val sortedComments = comments.sortedByDescending { it.timestamp }

                    posts.add(
                        Item(
                            id = obj.getString("id"),
                            profileImagePath = obj.optString("profileImagePath", null),
                            username = obj.getString("username"),
                            description = obj.getString("description"),
                            isLiked = obj.getBoolean("isLiked"),
                            comments = sortedComments.toMutableList(),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                posts.sortByDescending { it.timestamp }

            } catch (e: Exception) {
                prefs.edit().remove("postList").apply()
            }
        }
    }

    fun updateProfileImage(context: Context, username: String, newProfileImagePath: String?) {
        var updated = false

        posts.forEach { post ->
            if (post.username == username) {
                post.profileImagePath = newProfileImagePath
                updated = true
            }

            post.comments.forEach { comment ->
                if (comment.username == username) {
                    comment.profileImagePath = newProfileImagePath
                    updated = true
                }
            }
        }

        if (updated) {
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

            item.comments.forEach { comment ->
                if (comment.username == oldUsername) {
                    comment.username = newUsername
                    updated = true
                }
            }
        }

        if (updated) {
            savePosts(context)
        }
    }
}