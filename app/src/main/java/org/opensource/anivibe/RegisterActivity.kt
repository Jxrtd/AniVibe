package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.*

class RegisterActivity : Activity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_signup)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val usernameInput = findViewById<EditText>(R.id.edittext_username)
        val passwordInput = findViewById<EditText>(R.id.edittext_password)
        val emailInput = findViewById<EditText>(R.id.edittext_email)
        val confirmPasswordInput = findViewById<EditText>(R.id.edittext_comfirmpass)
        val createAccountButton = findViewById<Button>(R.id.createAcc)
        val loginText = findViewById<TextView>(R.id.anivibe_login)

        createAccountButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (!validateInput(username, email, password, confirmPassword)) return@setOnClickListener

            sharedPreferences.edit().apply {
                putString("username", username)
                putString("password", password)
                putString("email", email)
                apply()
            }

            Intent(this, LoginActivity::class.java).also {
                it.putExtra("username", username)
                it.putExtra("password", password)
                startActivity(it)
            }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInput(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill out the entire form.")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.")
            return false
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters long.")
            return false
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match.")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
