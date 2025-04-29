package org.opensource.anivibe.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.ToPostActivity
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.ItemAdapter
import org.opensource.anivibe.PostRepository
import org.opensource.anivibe.UserRepository
import org.opensource.anivibe.helper.ProfileImageUtils.loadProfileImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LandingPageFragment : Fragment(R.layout.anivibe_landingpagefragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var emptyState: View
    private val itemList = mutableListOf<Item>()

    private val postActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshPosts()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyState = view.findViewById(R.id.emptyState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        itemAdapter = ItemAdapter(
            requireContext(),
            itemList,
            onDeleteClickListener = { position -> deletePost(position) },
            onLikeClickListener = { position ->
                if (position in itemList.indices) {
                    itemList[position].isLiked = !itemList[position].isLiked
                    PostRepository.toggleLike(itemList[position].id ?: "")
                    itemAdapter.notifyItemChanged(position)
                }
            },
            onCommentClickListener = { position ->
                if (position in itemList.indices) {
                    val post = itemList[position]
                    val commentsFragment = CommentFragment.newInstance(
                        postId = post.id ?: "",
                        profileImagePath = post.profileImagePath,
                        username = post.username,
                        description = post.description,
                        timestamp = post.timestamp // 🔥 Pass timestamp here!
                    )
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer1, commentsFragment)
                        .addToBackStack(null)
                        .commit()
                }
            },
            timestampFormatter = { timestamp ->
                formatTimestamp(timestamp)
            }

        )

        recyclerView.adapter = itemAdapter
        refreshPosts()

        view.findViewById<View>(R.id.post).setOnClickListener {
            val intent = Intent(requireContext(), ToPostActivity::class.java)
            postActivityLauncher.launch(intent)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
    }

    override fun onResume() {
        super.onResume()
        refreshPosts()

        UserRepository.addProfileUpdateListener {
            refreshPosts()
        }
    }

    override fun onPause() {
        super.onPause()
        // Remove the listener when the fragment is not visible to prevent memory leaks
        UserRepository.removeProfileUpdateListener {
            refreshPosts()
        }
    }

    private fun refreshPosts() {
        try {
            val posts = PostRepository.getPosts(requireContext())
            itemList.clear()
            itemList.addAll(posts)
            itemAdapter.notifyDataSetChanged()

            // Show empty state if no posts
            if (itemList.isEmpty()) {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            // No need to manually load profile image here anymore
        } catch (e: Exception) {
            e.printStackTrace()
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }



    private fun deletePost(position: Int) {
        if (position in itemList.indices) {
            val postId = itemList[position].id ?: return
            try {
                PostRepository.deletePost(requireContext(), postId)
                itemList.removeAt(position)
                itemAdapter.notifyItemRemoved(position)

                // Check if we need to show empty state after deletion
                if (itemList.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}