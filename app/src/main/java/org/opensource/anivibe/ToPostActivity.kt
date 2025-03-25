package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class ToPostActivity : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topost)

        val cancel = findViewById<TextView>(R.id.cancel)

        cancel.setOnClickListener {
            Log.d("CSIT 284", "Sign-Up Clicked")
            val intent = Intent(this, NavBar::class.java)
            startActivity(intent)
        }
    }
}