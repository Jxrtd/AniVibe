package org.opensource.anivibe.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.opensource.anivibe.R

class MyDetailsFragment : Fragment(R.layout.mydetails) {

    private lateinit var educationTextView: TextView
    private lateinit var hometownTextView: TextView
    private lateinit var locationTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TextViews
        educationTextView = view.findViewById(R.id.educationTextView)
        hometownTextView = view.findViewById(R.id.hometownTextView)
        locationTextView = view.findViewById(R.id.locationTextView)

        // Load user details
        loadUserDetails()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment resumes
        loadUserDetails()
    }

    private fun loadUserDetails() {
        val detailsPrefs = requireContext().getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE)

        // Get stored values with fallback text
        val education = detailsPrefs.getString("education", "No details available")
        val hometown = detailsPrefs.getString("hometown", "No details available")
        val location = detailsPrefs.getString("location", "No details available")

        // Update UI
        educationTextView.text = education
        hometownTextView.text = hometown
        locationTextView.text = location
    }
}