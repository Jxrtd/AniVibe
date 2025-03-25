package org.opensource.anivibe.repository

import android.content.Context
import org.opensource.anivibe.data.Item

object PostRepository {
    private val posts = mutableListOf<Item>()

    fun addPost(context: Context, post: Item) {
        posts.add(0, post)
        savePosts(context)
    }

    fun getPosts(context: Context): List<Item> {
        if (posts.isEmpty()) {
            loadPosts(context)
        }
        return posts
    }

    fun deletePost(context: Context, position: Int) {
        if (position in posts.indices) {
            posts.removeAt(position)
            savePosts(context)
        }
    }

    private fun savePosts(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Posts", Context.MODE_PRIVATE)
        val postString = posts.joinToString("\n") { "${it.username}|${it.profileImageResId}|${it.description}" }
        with(sharedPreferences.edit()) {
            putString("postList", postString)
            apply()
        }
    }

    private fun loadPosts(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Posts", Context.MODE_PRIVATE)
        val postString = sharedPreferences.getString("postList", "") ?: ""
        posts.clear()
        if (postString.isNotEmpty()) {
            postString.split("\n").forEach { line ->
                val parts = line.split("|")
                if (parts.size == 3) {
                    posts.add(Item(parts[0], parts[1].toInt(), parts[2]))
                }
            }
        }
    }
}
