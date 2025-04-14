package org.opensource.anivibe.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.ToPostActivity
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.ItemAdapter
import org.opensource.anivibe.repository.PostRepository

class PostFragment : Fragment(R.layout.anivibe_landingpagefragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val posts = mutableListOf<Item>()
    private val POST_REQUEST_CODE = 1001

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load posts from repository
        loadPosts()

        // Set up adapter with delete callback
        adapter = ItemAdapter(
            requireContext(),
            posts
        ) { position ->
            // Handle delete
            PostRepository.deletePost(requireContext(), position)
            adapter.removeItem(position)
        }

        recyclerView.adapter = adapter

        // Set up post button
        val postButton = view.findViewById<ImageButton>(R.id.post)
        postButton.setOnClickListener {
            // Launch the ToPostActivity
            val intent = Intent(requireContext(), ToPostActivity::class.java)
            startActivityForResult(intent, POST_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Refresh posts when returning from create post screen
            loadPosts()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh posts when fragment becomes visible again
        loadPosts()
        adapter.notifyDataSetChanged()
    }

    private fun loadPosts() {
        posts.clear()
        posts.addAll(PostRepository.getPosts(requireContext()))
    }
}