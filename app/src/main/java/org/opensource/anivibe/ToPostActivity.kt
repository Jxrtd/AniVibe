package org.opensource.anivibe

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import org.opensource.anivibe.data.Item
import java.io.File
import java.util.UUID

class ToPostActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topost)

        val cancel = findViewById<ImageButton>(R.id.cancel)
        val postButton = findViewById<Button>(R.id.topost)
        val postContent = findViewById<EditText>(R.id.post_context)

        cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        postButton.setOnClickListener {
            val content = postContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(this, "Post cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val username = userPrefs.getString("username", "@user") ?: "@user"

            val profilePrefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
            val filename = profilePrefs.getString("profile_image", null)
            val imagePath = filename?.let { File(filesDir, it).absolutePath }

            val newItem = Item(
                id = UUID.randomUUID().toString(),
                profileImagePath = imagePath,
                username = username,
                description = content
            )

            PostRepository.addPost(applicationContext, newItem)

            setResult(RESULT_OK)
            finish()
        }
    }
}