package org.opensource.anivibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.UserRepository
import org.opensource.anivibe.data.Comment
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.CommentAdapter
import org.opensource.anivibe.helper.ProfileImageUtils
import org.opensource.anivibe.PostRepository
import org.opensource.anivibe.helper.CircleTransform
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CommentFragment : Fragment(R.layout.fragment_comment) {
    private val TAG = "CommentFragment"
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: Button
    private lateinit var adapter: CommentAdapter
    private lateinit var comments: MutableList<Comment>
    private lateinit var postId: String
    private var postTimestamp: Long = 0L
    private var currentPost: Item? = null

    private val profileUpdateListener = { updateProfileImages() }

    companion object {
        private const val ARG_POST_ID = "post_id"
        private const val ARG_PROFILE_IMAGE = "profile_image"
        private const val ARG_USERNAME = "username"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_TIMESTAMP = "timestamp"

        fun newInstance(
            postId: String,
            profileImagePath: String?,
            username: String,
            description: String,
            timestamp: Long
        ): CommentFragment {
            return CommentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POST_ID, postId)
                    putString(ARG_PROFILE_IMAGE, profileImagePath)
                    putString(ARG_USERNAME, username)
                    putString(ARG_DESCRIPTION, description)
                    putLong(ARG_TIMESTAMP, timestamp)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString(ARG_POST_ID) ?: run {
                Log.e(TAG, "Missing post ID argument")
                requireActivity().finish()
                return
            }
            postTimestamp = it.getLong(ARG_TIMESTAMP, 0L)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initializeViews(view)
            setupPostView(view)
            setupRecyclerView()
            setupCommentInput()
            setupLikeButton()
            setupDeleteButton()
            setupSendButton()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing fragment", e)
            showErrorSnackbar("Error loading comments")
            requireActivity().onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        UserRepository.addProfileUpdateListener(profileUpdateListener)
    }

    override fun onPause() {
        super.onPause()
        UserRepository.removeProfileUpdateListener(profileUpdateListener)
    }

    private fun updateProfileImages() {
        view?.findViewById<ImageView>(R.id.profile_pic_post)?.let { imageView ->
            val currentUser = UserRepository.getCurrentUser(requireContext())
            ProfileImageUtils.loadProfileImage(requireContext(), imageView, currentUser.profileImagePath)
        }
        adapter.notifyDataSetChanged()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.comments_recycler)
        commentInput = view.findViewById(R.id.comment_input)
        sendButton = view.findViewById(R.id.comment_send_button)

        sendButton.setBackgroundColor(requireContext().getColor(R.color.accentRed))
        sendButton.setTextColor(requireContext().getColor(R.color.white))
        sendButton.gravity = Gravity.CENTER
    }

    private fun setupPostView(view: View) {
        val post = PostRepository.getPost(postId) ?: run {
            showErrorSnackbar("Post not found")
            requireActivity().onBackPressed()
            return
        }

        currentPost = post
        comments = post.comments.toMutableList()

        val profileImage = view.findViewById<ImageView>(R.id.profile_pic_post)
        val currentUser = UserRepository.getCurrentUser(requireContext())
        ProfileImageUtils.loadProfileImage(requireContext(), profileImage, currentUser.profileImagePath)

        view.findViewById<TextView>(R.id.itemusername).text = post.username
        view.findViewById<TextView>(R.id.content).text = post.description

        val timestampTextView = view.findViewById<TextView>(R.id.post_timestamp)
        timestampTextView.text = formatTimestamp(post.timestamp)
    }

    private fun setupRecyclerView() {
        val currentUsername = UserRepository.getCurrentUsername(requireContext())
        adapter = CommentAdapter(comments, currentUsername).apply {
            setOnCommentDeleteListener(object : CommentAdapter.OnCommentDeleteListener {
                override fun onCommentDeleted(position: Int) {
                    deleteComment(position)
                }
            })
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CommentFragment.adapter
            setHasFixedSize(true)

            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom && adapter?.itemCount ?: 0 > 0) {
                    post {
                        val lastPosition = (adapter?.itemCount ?: 1) - 1
                        if (lastPosition >= 0) {
                            smoothScrollToPosition(lastPosition)
                        }
                    }
                }
            }
        }
    }
    private fun deleteComment(position: Int) {
        if (position in 0 until comments.size) {
            val commentToDelete = comments[position]
            comments.removeAt(position)
            adapter.notifyItemRemoved(position)
            PostRepository.deleteComment(requireContext(), postId, commentToDelete)
            showSuccessSnackbar("Comment deleted")
        }
    }
    private fun setupCommentInput() {
        commentInput.addTextChangedListener {
            sendButton.isEnabled = !it.isNullOrBlank()
        }
    }

    private fun setupLikeButton() {
        val likeButton = view?.findViewById<ImageButton>(R.id.likebtn)
        likeButton?.setOnClickListener {
            PostRepository.toggleLike(postId)
            val updatedPost = PostRepository.getPost(postId)
            updatedPost?.let {
                updateLikeButtonAppearance(likeButton, it.isLiked)
            }
        }
        updateLikeButtonAppearance(likeButton, currentPost?.isLiked ?: false)
    }

    private fun setupDeleteButton() {
        view?.findViewById<ImageView>(R.id.btndelete)?.setOnClickListener {
            PostRepository.deletePost(requireContext(), postId)
            parentFragmentManager.popBackStack()
            showSuccessSnackbar("Post deleted")
        }
    }

    private fun setupSendButton() {
        val post = PostRepository.getPost(postId) ?: run {
            showErrorSnackbar("Post not found")
            requireActivity().onBackPressed()
            return
        }
        sendButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotBlank()) {
                val context = requireContext()

                val currentUsername = UserRepository.getCurrentUsername(context)
                val currentUser = UserRepository.getCurrentUser(context)

                if (currentUser.username != currentUsername) {
                    currentUser.username = currentUsername
                    UserRepository.saveUser(context, currentUser)
                }

                val newComment = Comment(
                    username = post.username,
                    content = commentText,
                    profileImagePath = currentUser.profileImagePath,
                    userId = currentUsername,
                    timestamp = System.currentTimeMillis()
                )

                comments.add(newComment)
                adapter.notifyItemInserted(comments.size - 1)
                recyclerView.smoothScrollToPosition(comments.size - 1)
                commentInput.text.clear()

                PostRepository.addComment(requireContext(), postId, newComment)
            }
        }
    }

    private fun updateLikeButtonAppearance(button: ImageButton?, isLiked: Boolean) {
        button?.setImageResource(
            if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        )
        button?.setColorFilter(
            requireContext().getColor(
                if (isLiked) R.color.red else R.color.white
            )
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        return if (timestamp != 0L) {
            SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
        } else {
            ""
        }
    }

    private fun showErrorSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.dark_red))
                .show()
        }
    }

    private fun showSuccessSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(requireContext().getColor(R.color.accentRed))
                .show()
        }
    }
}
