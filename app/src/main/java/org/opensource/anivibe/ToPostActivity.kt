package org.opensource.anivibe

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.repository.PostRepository

class ToPostActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topost)

        val cancel = findViewById<TextView>(R.id.cancel)
        val postButton = findViewById<Button>(R.id.topost)
        val postContent = findViewById<EditText>(R.id.post_context)

        cancel.setOnClickListener { finish() }

        postButton.setOnClickListener {
            val content = postContent.text.toString().trim()
            if (content.isNotEmpty()) {
                val newItem = Item(
                    profileImageResId = R.drawable.profile_circle, // Example image
                    username = "@username", // Static username (replace with real user data)
                    description = content
                )

                PostRepository.addPost(applicationContext, newItem) // Pass context to save post
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}
