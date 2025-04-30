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

        educationTextView = view.findViewById(R.id.educationTextView)
        hometownTextView = view.findViewById(R.id.hometownTextView)
        locationTextView = view.findViewById(R.id.locationTextView)

        loadUserDetails()
    }

    override fun onResume() {
        super.onResume()
        loadUserDetails()
    }

    private fun loadUserDetails() {
        val detailsPrefs = requireContext().getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE)

        val education = detailsPrefs.getString("education", "No details available")
        val hometown = detailsPrefs.getString("hometown", "No details available")
        val location = detailsPrefs.getString("location", "No details available")

        educationTextView.text = education
        hometownTextView.text = hometown
        locationTextView.text = location
    }
}