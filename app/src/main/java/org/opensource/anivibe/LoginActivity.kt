package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_login) // Ensure correct layout is used

        // Sample hardcoded credentials (replace with actual authentication logic)
        val validUsername = "user123"
        val validPassword = "password123"

        // UI References
        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.anivibe_signup)
        val et_username = findViewById<EditText>(R.id.username)
        val et_password = findViewById<EditText>(R.id.password)

        intent?.let {
            it.getStringExtra("username")?.let {username ->
                et_username.setText(username)
            }

            it.getStringExtra("password")?.let {password ->
                et_password.setText(password)
            }
        }

        // Sign-Up Button Click Listener
        signupButton.setOnClickListener {
            Log.d("CSIT 284", "Sign-Up Clicked")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Login Button Click Listener
        loginButton.setOnClickListener {
            val enteredUsername = usernameInput.text.toString().trim()
            val enteredPassword = passwordInput.text.toString().trim()

            if (enteredUsername == et_username.text.toString() && enteredPassword == et_password.text.toString()) {
                Log.d("CSIT 284", "Login Successful - Navigating to Landing Page")
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                // Ensure activity starts correctly
                val intent = Intent(this, NavBar::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)


            } else {
                Log.d("CSIT 284", "Login Failed - Incorrect Credentials")
                Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
