package org.opensource.anivibe.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private val postActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadPosts()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up adapter with callbacks
        adapter = ItemAdapter(
            requireContext(),
            posts,
            onDeleteClickListener = { position ->
                if (position in 0 until posts.size) {
                    val postId = posts[position].id
                    if (postId != null) {
                        if (postId.isNotEmpty()) {
                            PostRepository.deletePost(requireContext(), postId)
                            posts.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            Toast.makeText(requireContext(), "Post deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onLikeClickListener = { position ->
                if (position in 0 until posts.size) {
                    val post = posts[position]
                    PostRepository.toggleLike(post.id)
                    post.isLiked = !post.isLiked
                    adapter.notifyItemChanged(position)
                }
            },
            onCommentClickListener = { position ->
                if (position in 0 until posts.size) {
                    val postId = posts[position].id
                    val commentsFragment = postId?.let { CommentFragment.newInstance(it) }
                    if (commentsFragment != null) {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer1, commentsFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        )

        recyclerView.adapter = adapter

        val postButton = view.findViewById<ImageButton>(R.id.post)
        postButton.setOnClickListener {
            val intent = Intent(requireContext(), ToPostActivity::class.java)
            postActivityLauncher.launch(intent)
        }

        loadPosts()
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
    }

    private fun loadPosts() {
        try {
            val loadedPosts = PostRepository.getPosts(requireContext())
            posts.clear()
            posts.addAll(loadedPosts)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error loading posts", Toast.LENGTH_SHORT).show()
        }
    }
}
