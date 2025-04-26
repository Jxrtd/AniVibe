package org.opensource.anivibe

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView
import org.opensource.anivibe.fragments.ProfileFragment

class SettingsActivity : AppCompatActivity() {

    private lateinit var profileImageView: CircleImageView
    private lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_settings)

        // Bind profile views
        profileImageView = findViewById(R.id.settings_profile_image)
        usernameTextView = findViewById(R.id.settings_username_text)

        // Set up back button
        findViewById<android.widget.ImageButton>(R.id.backbutton).setOnClickListener {
            finish()
        }

        // Set up username section to navigate to ProfileFragment
        findViewById<LinearLayout>(R.id.usernameSection).setOnClickListener {
            // Navigate to MainActivity and show ProfileFragment
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
        }

        // Set up edit profile button
        findViewById<LinearLayout>(R.id.editProfileButton).setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Set up edit anime preferences button
        findViewById<LinearLayout>(R.id.editAnimeButton).setOnClickListener {
            val intent = Intent(this, EditAnimeStatActivity::class.java)
            startActivity(intent)
        }

        // Set up help and support button
        findViewById<LinearLayout>(R.id.HelpAndSupportButton).setOnClickListener {
            // Handle support navigation
        }

        // Set up developer's page button
        findViewById<LinearLayout>(R.id.developerPageButton).setOnClickListener {
            // Handle developer page navigation
        }

        // Set up logout button
        findViewById<android.widget.Button>(R.id.logoutButton).setOnClickListener {
            // Handle logout
        }
    }

    override fun onResume() {
        super.onResume()
        // Load the latest profile data when returning to this activity
        loadProfileData()
    }

    private fun loadProfileData() {
        // Load username from UserPrefs
        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = userPrefs.getString("username", "username")
        usernameTextView.text = "@$username"

        // Load profile picture from ProfilePrefs
        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        profilePrefs.getString("profile_image", null)?.let { filename ->
            try {
                openFileInput(filename).use { fileInputStream ->
                    val bitmap = BitmapFactory.decodeStream(fileInputStream)
                    profileImageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                // If there's an error loading the image, keep the default
                e.printStackTrace()
            }
        }
    }
}