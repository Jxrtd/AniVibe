package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton

class DevelopersPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_developerpage)

        Log.d("CSIT 284", "DevelopersPageActivity Loaded Successfully")

        val back: ImageButton = findViewById(R.id.devbackbutton)
        back.setOnClickListener {
            Log.d("CSIT 284", "Back button clicked")
            finish()  // Just finish this activity to return to SettingsActivity
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    // Also override the system back button
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}