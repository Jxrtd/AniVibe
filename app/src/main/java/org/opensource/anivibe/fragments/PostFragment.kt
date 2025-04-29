package org.opensource.anivibe.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.ToPostActivity
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.ItemAdapter
import org.opensource.anivibe.UserRepository
import org.opensource.anivibe.PostRepository
import org.opensource.anivibe.helper.ProfileImageUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostFragment : Fragment(R.layout.anivibe_landingpagefragment) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val posts = mutableListOf<Item>()

    private val profileUpdateListener = {
        Log.d("PostFragment", "Profile update detected, reloading posts")
        loadPosts()
    }

    private val postActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadPosts()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView()
        setupPostButton(view)
        loadPosts()
    }

    override fun onResume() {
        super.onResume()
        UserRepository.addProfileUpdateListener(profileUpdateListener)
        loadPosts()
    }

    override fun onPause() {
        super.onPause()
        UserRepository.removeProfileUpdateListener(profileUpdateListener)
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ItemAdapter(
            requireContext(),
            posts,
            onDeleteClickListener = { position -> deletePost(position) },
            onLikeClickListener = { position -> toggleLike(position) },
            onCommentClickListener = { position -> openComments(position) },
            timestampFormatter = { timestamp -> formatTimestamp(timestamp) }
        )

        recyclerView.adapter = adapter
    }

    private fun setupPostButton(view: View) {
        view.findViewById<ImageButton>(R.id.post).setOnClickListener {
            val intent = Intent(requireContext(), ToPostActivity::class.java)
            postActivityLauncher.launch(intent)
        }
    }

    private fun loadPosts() {
        try {
            val context = requireContext()
            val loadedPosts = PostRepository.getPosts(context)
            val currentUser = UserRepository.getCurrentUser(context)
            val currentUsername = UserRepository.getCurrentUsername(context)

            Log.d("PostFragment", "Loading ${loadedPosts.size} posts for user: $currentUsername")

            posts.clear()
            posts.addAll(loadedPosts.map { post ->
                if (post.username == currentUsername) {
                    post.copy(profileImagePath = currentUser.profileImagePath)
                } else {
                    post // No update for other users
                }
            })

            adapter.notifyDataSetChanged()

            if (posts.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
            }
        } catch (e: Exception) {
            Log.e("PostFragment", "Error loading posts", e)
            Toast.makeText(requireContext(), "Error loading posts", Toast.LENGTH_SHORT).show()
        }
    }




    private fun deletePost(position: Int) {
        if (position in posts.indices) {
            val postId = posts[position].id
            if (!postId.isNullOrEmpty()) {
                try {
                    PostRepository.deletePost(requireContext(), postId)
                    posts.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(requireContext(), "Post deleted", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("PostFragment", "Failed to delete post", e)
                    Toast.makeText(requireContext(), "Failed to delete post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toggleLike(position: Int) {
        if (position in posts.indices) {
            val post = posts[position]
            post.isLiked = !post.isLiked
            post.id?.let { PostRepository.toggleLike(it) }
            adapter.notifyItemChanged(position)
        }
    }

    private fun openComments(position: Int) {
        if (position in posts.indices) {
            val post = posts[position]
            val commentsFragment = post.id?.let {
                CommentFragment.newInstance(
                    postId = it,
                    profileImagePath = post.profileImagePath,
                    username = post.username,
                    description = post.description,
                    timestamp = post.timestamp
                )
            }
            commentsFragment?.let {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer1, it)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}