package org.opensource.anivibe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.opensource.anivibe.R
import org.opensource.anivibe.data.Comment
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.CommentAdapter
import org.opensource.anivibe.repository.PostRepository

class CommentFragment : Fragment(R.layout.fragment_comment) {

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

        fun newInstance(postId: String, profileImagePath: String?): CommentFragment {
            val fragment = CommentFragment()
            val args = Bundle()
            args.putString(ARG_POST_ID, postId)
            args.putString(ARG_PROFILE_IMAGE, profileImagePath)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postId = arguments?.getString(ARG_POST_ID) ?: ""
        val fallbackProfileImagePath = arguments?.getString(ARG_PROFILE_IMAGE)
        val post = PostRepository.getPost(postId) ?: return
        currentPost = post
        comments = post.comments.toMutableList()

        val effectiveProfileImagePath = if (!post.profileImagePath.isNullOrBlank()) {
            post.profileImagePath
        } else {
            fallbackProfileImagePath
        }

        comments = post.comments.toMutableList()

        val postUserImage: ImageView = view.findViewById(R.id.profile_pic_post)
        val postUsername: TextView = view.findViewById(R.id.itemusername)
        val postContent: TextView = view.findViewById(R.id.content)
        val likeButton: ImageButton = view.findViewById(R.id.likebtn)
        val deleteButton: ImageView = view.findViewById(R.id.btndelete)

        postUsername.text = post.username
        postContent.text = post.description

        Picasso.get()
            .load(effectiveProfileImagePath)
            .placeholder(R.drawable.default_profile_pic)
            .error(R.drawable.default_profile_pic)
            .into(postUserImage)

        recyclerView = view.findViewById(R.id.comments_recycler)
        commentInput = view.findViewById(R.id.comment_input)
        sendButton = view.findViewById(R.id.comment_send_button)

        adapter = CommentAdapter(comments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

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

        sendButton.setOnClickListener {
            val commentText = commentInput.text.toString()
            if (commentText.isNotBlank()) {
                val comment = Comment(
                    username = post.username,
                    content = commentText,
                    profileImagePath = effectiveProfileImagePath
                )
                comments.add(comment)
                PostRepository.addComment(postId, comment)
                adapter.notifyItemInserted(comments.size - 1)
                recyclerView.scrollToPosition(comments.size - 1)
                commentInput.text.clear()
            }
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