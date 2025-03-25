package org.opensource.anivibe

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

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

            if (user.isEmpty() || pass.isEmpty() || emailText.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Fill out the form completely.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (pass != confirmPass) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            sharedPreferences.edit().apply {
                putString("username", user)
                putString("password", pass)
                apply()
            }

            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    putExtra("username", username.text.toString())
                    putExtra("password", password.text.toString())
                }
            )
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
