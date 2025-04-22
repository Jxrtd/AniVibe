package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : Activity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var storedUsername: String? = null
    private var storedPassword: String? = null
    private var registeredUsername: String? = null
    private var registeredPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_login)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize UI components
        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<TextView>(R.id.anivibe_signup)

        // Get stored credentials from SharedPreferences
        storedUsername = sharedPreferences.getString("username", null)
        storedPassword = sharedPreferences.getString("password", null)

        intent?.let {
            registeredUsername = it.getStringExtra("username") ?: ""
            registeredPassword = it.getStringExtra("password") ?: ""
        }
        // Auto-fill stored credentials if any
        usernameInput.setText(registeredUsername)
        passwordInput.setText(registeredPassword)

        // Signup button click
        signupButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Login button click
        loginButton.setOnClickListener {
            val enteredUsername = usernameInput.text.toString().trim()
            val enteredPassword = passwordInput.text.toString().trim()

            // Validate inputs
            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if credentials match
            if (enteredUsername == storedUsername && enteredPassword == storedPassword) {
                // Successful login
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NavBar::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Show error message
                Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
