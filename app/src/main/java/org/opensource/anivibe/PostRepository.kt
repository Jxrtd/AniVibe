// org/opensource/anivibe/repository/PostRepository.kt
package org.opensource.anivibe.repository

import android.content.Context
import org.opensource.anivibe.data.Item

object PostRepository {
    private var posts = mutableListOf<Item>()

    fun addPost(context: Context, post: Item) {
        posts.add(0, post)
        savePosts(context)
    }

    fun getPosts(context: Context): List<Item> {
        if (posts.isEmpty()) loadPosts(context)
        return posts
    }

    fun deletePost(context: Context, position: Int) {
        if (position in posts.indices) {
            posts.removeAt(position)
            savePosts(context)
        }
    }

    private fun savePosts(context: Context) {
        val prefs = context.getSharedPreferences("Posts", Context.MODE_PRIVATE)
        // delimiter "~|~" unlikely to appear in user text
        val serialized = posts.joinToString("||") { item ->
            listOf(
                item.username.replace("|", ""),             // sanitize
                item.profileImagePath ?: "",
                item.description.replace("|", "")
            ).joinToString("|")
        }
        prefs.edit()
            .putString("postList", serialized)
            .apply()
    }

    private fun loadPosts(context: Context) {
        val prefs = context.getSharedPreferences("Posts", Context.MODE_PRIVATE)
        val serialized = prefs.getString("postList", "") ?: ""
        posts.clear()
        if (serialized.isNotEmpty()) {
            serialized.split("||").forEach { entry ->
                val parts = entry.split("|")
                if (parts.size == 3) {
                    val username = parts[0]
                    val path     = parts[1].ifBlank { null }
                    val desc     = parts[2]
                    posts.add(Item(path, username, desc))
                }
            }
        }
    }

    // Add this method to PostRepository.kt
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
