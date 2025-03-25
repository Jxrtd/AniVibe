package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_signup)


        val username = findViewById<EditText>(R.id.edittext_username)
        val password = findViewById<EditText>(R.id.edittext_password)
        val email = findViewById<EditText>(R.id.edittext_email )
        val comfirmpass = findViewById<EditText>(R.id.edittext_comfirmpass)


        val button_submit = findViewById<Button>(R.id.createAcc)
        button_submit.setOnClickListener {

            if (username.text.toString().isNullOrEmpty()
                || password.text.toString().isNullOrEmpty()
                || email.text.toString().isNullOrEmpty()
                || password == comfirmpass) {
                Toast.makeText(this, "Fill out the form completely.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    putExtra("username", username.text.toString())
                    putExtra("password", password.text.toString())
                }
            )
        }
        
        val anivibe_login = findViewById<Button>(R.id.anivibe_login)
        anivibe_login.setOnClickListener {
            Log.e("CSIT 284", "Login")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}