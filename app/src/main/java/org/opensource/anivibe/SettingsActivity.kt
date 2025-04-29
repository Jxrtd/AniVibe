package org.opensource.anivibe

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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

        // Back button - always finishes the activity
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Username section
        findViewById<LinearLayout>(R.id.usernameSection).setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Edit profile
        findViewById<LinearLayout>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Edit anime preferences
        findViewById<LinearLayout>(R.id.editAnimeButton).setOnClickListener {
            startActivity(Intent(this, EditAnimeStatActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Help and support
        findViewById<LinearLayout>(R.id.HelpAndSupportButton).setOnClickListener {
            startActivity(Intent(this, SettingsHelpAndSupportAcitivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Developer's page
        findViewById<LinearLayout>(R.id.developerPageButton).setOnClickListener {
            startActivity(Intent(this, DevelopersPageActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Logout with confirmation
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            showLogoutConfirmation()
        }
    }

    // Override the back button press to always finish the activity
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    private fun loadProfileData() {
        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = userPrefs.getString("username", "username")
        usernameTextView.text = "@$username"

        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        profilePrefs.getString("profile_image", null)?.let { filename ->
            try {
                openFileInput(filename).use { fileInputStream ->
                    val bitmap = BitmapFactory.decodeStream(fileInputStream)
                    profileImageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showLogoutConfirmation() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Logout")
        dialogBuilder.setMessage("Are you sure you want to logout?")
        dialogBuilder.setPositiveButton("Logout") { _: DialogInterface, _: Int ->
            performLogout()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun performLogout() {

        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}