package org.opensource.anivibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.UserRepository
import org.opensource.anivibe.data.Comment
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.CommentAdapter
import org.opensource.anivibe.helper.CircleTransform
import org.opensource.anivibe.repository.PostRepository
import java.io.File

class CommentFragment : Fragment(R.layout.fragment_comment) {
    private val TAG = "CommentFragment"

    private lateinit var recyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: Button
    private lateinit var adapter: CommentAdapter
    private lateinit var comments: MutableList<Comment>
    private lateinit var postId: String
    private var currentPost: Item? = null

    companion object {
        private const val ARG_POST_ID = "post_id"
        private const val ARG_PROFILE_IMAGE = "profile_image"
        private const val ARG_USERNAME = "username"
        private const val ARG_DESCRIPTION = "description"

        fun newInstance(postId: String, profileImagePath: String?, username: String, description: String): CommentFragment {
            Log.d("CommentFragment", "Creating new instance with: postId=$postId, username=$username, desc=${description.take(20)}...")

            val fragment = CommentFragment()
            val args = Bundle()
            args.putString(ARG_POST_ID, postId)
            args.putString(ARG_PROFILE_IMAGE, profileImagePath)
            args.putString(ARG_USERNAME, username)
            args.putString(ARG_DESCRIPTION, description)
            fragment.arguments = args

            // Verify the arguments were set correctly
            Log.d("CommentFragment", "Arguments set: ${args.getString(ARG_USERNAME)}, ${args.getString(ARG_DESCRIPTION)?.take(20)}")

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Arguments received: ${arguments?.keySet()?.joinToString()}")
        Log.d(TAG, "onCreate: postId=${arguments?.getString(ARG_POST_ID)}")
        Log.d(TAG, "onCreate: username=${arguments?.getString(ARG_USERNAME)}")
        Log.d(TAG, "onCreate: description=${arguments?.getString(ARG_DESCRIPTION)?.take(20)}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Log all arguments to make sure they're being passed correctly
        Log.d(TAG, "onViewCreated: Arguments bundle: ${arguments != null}")

        postId = arguments?.getString(ARG_POST_ID) ?: ""
        val profileImagePath = arguments?.getString(ARG_PROFILE_IMAGE)
        val username = arguments?.getString(ARG_USERNAME) ?: ""
        val description = arguments?.getString(ARG_DESCRIPTION) ?: ""

        Log.d(TAG, "onViewCreated: Retrieved postId=$postId")
        Log.d(TAG, "onViewCreated: Retrieved username=$username")
        Log.d(TAG, "onViewCreated: Retrieved description=${description.take(20)}...")

        // Verify we can get the post
        val post = PostRepository.getPost(postId)
        if (post == null) {
            Log.e(TAG, "onViewCreated: Failed to retrieve post with ID: $postId")
            Toast.makeText(requireContext(), "Failed to load post", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "onViewCreated: Found post with username=${post.username}, description=${post.description.take(20)}...")

        currentPost = post
        comments = post.comments.toMutableList()

        val currentUser = UserRepository.getCurrentUser(requireContext())

        try {
            // Find views
            val postUserImage: ImageView = view.findViewById(R.id.profile_pic_post)
            val postUsername: TextView = view.findViewById(R.id.itemusername)
            val postContent: TextView = view.findViewById(R.id.content)
            val likeButton: ImageButton = view.findViewById(R.id.likebtn)
            val deleteButton: ImageView = view.findViewById(R.id.btndelete)

            Log.d(TAG, "onViewCreated: Views found")

            // Use the latest username from the post object, not the arguments
            // This ensures we're using the most up-to-date username
            postUsername.text = post.username
            postContent.text = post.description

            Log.d(TAG, "onViewCreated: Username and description set to views")

            // Debug what was actually set
            Log.d(TAG, "onViewCreated: TextView username is now: ${postUsername.text}")
            Log.d(TAG, "onViewCreated: TextView content is now: ${postContent.text}")

            // Load profile image for the post
            if (!post.profileImagePath.isNullOrBlank()) {
                loadProfileImage(postUserImage, post.profileImagePath)
            } else {
                postUserImage.setImageResource(R.drawable.profile_circle)
            }

            recyclerView = view.findViewById(R.id.comments_recycler)
            commentInput = view.findViewById(R.id.comment_input)
            sendButton = view.findViewById(R.id.comment_send_button)


            sendButton.setBackgroundColor(requireContext().getColor(R.color.accentRed))
            sendButton.setTextColor(requireContext().getColor(R.color.white))


            // Center the send button (add this line to apply center gravity)
            sendButton.gravity = android.view.Gravity.CENTER

            adapter = CommentAdapter(comments)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter

            Log.d(TAG, "onViewCreated: Comment adapter set with ${comments.size} comments")

            updateLikeButtonAppearance(likeButton, post.isLiked)

            likeButton.setOnClickListener {
                PostRepository.toggleLike(postId)
                val updatedPost = PostRepository.getPost(postId)
                updatedPost?.let {
                    updateLikeButtonAppearance(likeButton, it.isLiked)
                }
            }

            deleteButton.setOnClickListener {
                PostRepository.deletePost(requireContext(), postId)
                parentFragmentManager.popBackStack()
            }

            // In CommentFragment.kt, update the sendButton.setOnClickListener block
            sendButton.setOnClickListener {
                val commentText = commentInput.text.toString()
                if (commentText.isNotBlank()) {
                    // Create a new comment with the current user's information
                    val comment = Comment(
                        username = currentUser.username,
                        content = commentText,
                        profileImagePath = currentUser.profileImagePath,
                        userId = currentUser.username  // Always set userId to the current username
                    )
                    comments.add(comment)
                    PostRepository.addComment(postId, comment)
                    adapter.notifyItemInserted(comments.size - 1)
                    recyclerView.scrollToPosition(comments.size - 1)
                    commentInput.text.clear()

                    // Log successful comment addition
                    Log.d(TAG, "Added comment from ${currentUser.username} with profile image: ${currentUser.profileImagePath}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "Error loading comment view", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage(imageView: ImageView, profileImagePath: String?) {
        Log.d(TAG, "loadProfileImage: Attempting to load from $profileImagePath")

        if (!profileImagePath.isNullOrBlank()) {
            try {
                // First check if this is a full path or just a filename
                val imageFile = if (profileImagePath.contains("/")) {
                    File(profileImagePath)
                } else {
                    File(requireContext().filesDir, profileImagePath)
                }

                if (imageFile.exists()) {
                    Picasso.get()
                        .load(imageFile)
                        .placeholder(R.drawable.profile_circle)
                        .error(R.drawable.profile_circle)
                        .transform(CircleTransform())
                        .into(imageView)
                    Log.d(TAG, "loadProfileImage: Successfully loaded local file")
                } else {
                    // Try as URL or resource if file doesn't exist
                    Picasso.get()
                        .load(profileImagePath)
                        .placeholder(R.drawable.profile_circle)
                        .error(R.drawable.profile_circle)
                        .transform(CircleTransform())
                        .into(imageView)
                    Log.d(TAG, "loadProfileImage: Attempting to load as URL/resource")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile image", e)
                imageView.setImageResource(R.drawable.profile_circle)
            }
        } else {
            Log.d(TAG, "loadProfileImage: No profile image path, using default")
            imageView.setImageResource(R.drawable.profile_circle)
        }
    }

    private fun updateLikeButtonAppearance(button: ImageButton, isLiked: Boolean) {
        if (isLiked) {
            button.setImageResource(R.drawable.ic_heart_filled)
            button.setColorFilter(requireContext().getColor(R.color.red))
        } else {
            button.setImageResource(R.drawable.ic_heart_outline)
            button.setColorFilter(requireContext().getColor(R.color.white))
        }
    }
}