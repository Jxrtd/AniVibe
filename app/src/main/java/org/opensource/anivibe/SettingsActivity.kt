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
import android.widget.Toast
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

        profileImageView = findViewById(R.id.settings_profile_image)
        usernameTextView = findViewById(R.id.settings_username_text)

        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        findViewById<LinearLayout>(R.id.usernameSection).setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.editAnimeButton).setOnClickListener {
            startActivity(Intent(this, EditAnimeStatActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.HelpAndSupportButton).setOnClickListener {
            startActivity(Intent(this, SettingsHelpAndSupportAcitivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<LinearLayout>(R.id.developerPageButton).setOnClickListener {
            startActivity(Intent(this, DevelopersPageActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            showLogoutConfirmation()
        }
    }

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
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnLogout = dialogView.findViewById<Button>(R.id.btnLogout)

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()

        dialog.setCanceledOnTouchOutside(false)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnLogout.setOnClickListener {
            val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            userPrefs.edit().clear().apply()

            getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE).edit().clear().apply()
            getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE).edit().clear().apply()

            Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            finish()
        }

        dialog.show()
    }

}