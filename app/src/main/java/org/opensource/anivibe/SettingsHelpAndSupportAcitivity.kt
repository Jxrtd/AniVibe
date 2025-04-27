package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView

class SettingsHelpAndSupportAcitivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_help_and_support_acitivity)

        val lvHelpAndSupport = findViewById<ListView>(R.id.lvHelpAndSupport)

        val helpAndSupportList = listOf(
            " - Frequently Asked Questions",
            " - Contact Support",
            " - Terms and Conditions",
            " - Privacy Policy",
            " - Send Feedback",
            " - Troubleshooting Guide",
            " - App Updates & Changelog"
        )

        val arrayAdapter = ArrayAdapter(
            this,
            R.layout.simple_list_item,
            R.id.list_item_text,
            helpAndSupportList
        )
        lvHelpAndSupport.adapter = arrayAdapter

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
