package org.opensource.anivibe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_settings)

        Log.d("CSIT 284", "SettingsActivity Loaded Successfully")

        val menu: ImageButton = findViewById(R.id.backbutton)
        menu.setOnClickListener {
            Log.d("CSIT 284", "Back button clicked")

            val intent = Intent(this, NavBar::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val dev: LinearLayout = findViewById(R.id.developerPageButton)
        dev.setOnClickListener {
            Log.d("CSIT 284", "Developer's Page button clicked")

            val intent = Intent(this, DevelopersPageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        val support: LinearLayout = findViewById(R.id.HelpAndSupportButton)
        support.setOnClickListener {
            Log.d("CSIT 284", "Developer's Page button clicked")

            val intent = Intent(this, Settings_HelpAndSupportAcitivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }



        val logout = findViewById<Button>(R.id.logoutButton)
        logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    Log.e("CSIT 284", "Logging out")
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null) // Dismisses the dialog
                .show()
        }
    }
}
