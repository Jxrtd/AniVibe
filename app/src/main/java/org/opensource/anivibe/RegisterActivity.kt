package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Patterns

class RegisterActivity : Activity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anivibe_signup)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val username = findViewById<EditText>(R.id.edittext_username)
        val password = findViewById<EditText>(R.id.edittext_password)
        val email = findViewById<EditText>(R.id.edittext_email)
        val confirmPassword = findViewById<EditText>(R.id.edittext_comfirmpass)
        val buttonSubmit = findViewById<Button>(R.id.createAcc)
        val loginButton = findViewById<Button>(R.id.anivibe_login)

        buttonSubmit.setOnClickListener {
            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()
            val emailText = email.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (!validateInput(user, emailText, pass, confirmPass)) return@setOnClickListener

            sharedPreferences.edit().apply {
                putString("username", user)
                putString("password", pass)
                apply()
            }

            startActivity(Intent(this, LoginActivity::class.java).apply {
                putExtra("username", user)
                putExtra("password", pass)
            })
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateInput(user: String, email: String, pass: String, confirmPass: String): Boolean {
        if (user.isEmpty() || pass.isEmpty() || email.isEmpty() || confirmPass.isEmpty()) {
            showToast("Fill out the form completely.")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email address.")
            return false
        }

        if (pass.length < 6) {
            showToast("Password must be at least 6 characters.")
            return false
        }

        if (pass != confirmPass) {
            showToast("Passwords do not match!")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
