package org.opensource.anivibe

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    // Views
    private lateinit var profileImageView: CircleImageView
    private lateinit var profilePicBtn: MaterialButton
    private lateinit var nameInput: EditText
    private lateinit var bioInput: EditText
    private lateinit var changePassBtn: MaterialButton

    private lateinit var educationInput: EditText
    private lateinit var hometownInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var birthdateInput: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton

    // SharedPreferences
    private lateinit var userPrefs: SharedPreferences
    private lateinit var profilePrefs: SharedPreferences
    private lateinit var detailsPrefs: SharedPreferences

    private var currentPhotoUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    // Camera / Gallery launchers
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok && currentPhotoUri != null) loadImageFromUri(currentPhotoUri!!)
        }
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { loadImageFromUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // 1) initialize your SharedPreferences
        userPrefs    = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        detailsPrefs = getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE)

        // 2) bind your views
        profileImageView = findViewById(R.id.setting_profile_image)
        profilePicBtn    = findViewById(R.id.profilepic)
        nameInput        = findViewById(R.id.input_name)
        bioInput         = findViewById(R.id.input_bio)
        changePassBtn    = findViewById(R.id.changepassword)
        backButton       = findViewById(R.id.backButton)

        educationInput   = findViewById(R.id.input_education)
        hometownInput    = findViewById(R.id.input_hometown)
        locationInput    = findViewById(R.id.input_location)
        birthdateInput   = findViewById(R.id.input_birthdate)
        saveButton       = findViewById(R.id.button_save)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }

        // 3) preload existing values into the UI
        // — profile picture
        profilePrefs.getString("profile_image", null)?.let { fn ->
            openFileInput(fn).use { fis ->
                profileImageView.setImageBitmap(BitmapFactory.decodeStream(fis))
            }
        }

        // — populate input fields with current values
        nameInput.setText(userPrefs.getString("username", ""))
        bioInput.setText(userPrefs.getString("bio", ""))

        // — "My Details" fields
        educationInput.setText(detailsPrefs.getString("education", ""))
        hometownInput.setText(detailsPrefs.getString("hometown", ""))
        locationInput.setText(detailsPrefs.getString("location", ""))
        birthdateInput.setText(detailsPrefs.getString("birthdate", ""))

        // 4) wire up click listeners
        profilePicBtn.setOnClickListener    { onChangePicture() }
        profileImageView.setOnClickListener { onChangePicture() }
        changePassBtn.setOnClickListener    { showPasswordChangeDialog() }

        // In EditProfileActivity.kt, modify the saveButton.setOnClickListener block
        saveButton.setOnClickListener {
            val oldUsername = userPrefs.getString("username", "") ?: ""
            val newUsername = nameInput.text.toString().trim()
            val bio = bioInput.text.toString().trim()

            // Only update posts if username has changed
            if (oldUsername != newUsername) {
                // Update all posts with the new username
                PostRepository.updateUsername(this, oldUsername, newUsername)
            }

            userPrefs.edit().apply {
                putString("username", newUsername)
                putString("bio", bio)
                apply()
            }

            detailsPrefs.edit().apply {
                putString("education", educationInput.text.toString())
                putString("hometown", hometownInput.text.toString())
                putString("location", locationInput.text.toString())
                putString("birthdate", birthdateInput.text.toString())
                apply()
            }

            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            finish()
        }

        progressDialog = ProgressDialog(this).apply {
            setCanceledOnTouchOutside(false)
        }

        // Hide default action bar since we're using custom navigation
        supportActionBar?.hide()
    }

    private fun loadImageFromUri(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { stream ->
            val bmp = BitmapFactory.decodeStream(stream)
            profileImageView.setImageBitmap(bmp)

            // save the new picture
            val filename = "profile_${System.currentTimeMillis()}.png"
            openFileOutput(filename, Context.MODE_PRIVATE).use { out ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            profilePrefs.edit().putString("profile_image", filename).apply()
        }
    }

    private fun showPasswordChangeDialog() {
        val dlg = layoutInflater.inflate(R.layout.dialog_update_password, null)
        val oldEt = dlg.findViewById<EditText>(R.id.etOldPassword)
        val newEt = dlg.findViewById<EditText>(R.id.etNewPassword)
        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dlg)
            .setPositiveButton("Change") { _, _ ->
                val oldPass = oldEt.text.toString()
                val newPass = newEt.text.toString()
                val passPrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                if (passPrefs.getString("password","") == oldPass) {
                    passPrefs.edit().putString("password", newPass).apply()
                    Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Incorrect old password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        private const val REQUEST_PERM_CODE = 123
    }

    private fun onChangePicture() {
        // if we don't have camera / storage permission yet, request it
        if (!hasPermissions()) {
            requestPermissions(requiredPermissions, REQUEST_PERM_CODE)
            return
        }

        // otherwise show a simple chooser
        AlertDialog.Builder(this)
            .setTitle("Choose Image Source")
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                when (which) {
                    0 -> launchCamera()
                    1 -> pickImageLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun hasPermissions(): Boolean = requiredPermissions.all { perm ->
        ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
    }

    private val requiredPermissions: Array<String>
        get() = arrayOf(
            Manifest.permission.CAMERA,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        )

    private fun launchCamera() {
        // create a temp file for the camera to write into
        val file = createImageFile() ?: return
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )
        takePictureLauncher.launch(currentPhotoUri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    // handle the permission dialog result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERM_CODE &&
            grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            // user just granted camera/storage → try again
            onChangePicture()
        }
    }
}