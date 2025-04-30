package org.opensource.anivibe

import android.app.Activity
import android.os.Bundle
import android.widget.ImageButton

class DevelopersPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_developerpage)
        val back: ImageButton = findViewById(R.id.devbackbutton)
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}